package onlymash.materixiv.ui.module.illust

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.action.Duration
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.MyDatabase
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.illust.IllustRepositoryImpl
import onlymash.materixiv.extensions.asMergedLoadStates
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.extensions.getWindowWidth
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.NetworkLoadStateAdapter
import onlymash.materixiv.ui.module.common.SharedViewModelFragment
import org.kodein.di.instance
import retrofit2.HttpException
import kotlin.math.roundToInt

class IllustFragment : SharedViewModelFragment() {

    companion object {
        fun create(type: Int, word: String? = null, userId: String? = null, illustId: Long = -1): IllustFragment {
            return IllustFragment().apply {
                arguments = Bundle().apply {
                    putInt(Keys.PAGE_TYPE, type)
                    putString(Keys.SEARCH_WORD, word)
                    putString(Keys.USER_ID, userId)
                    putLong(Keys.ILLUST_ID, illustId)
                }
            }
        }
    }

    private val db by instance<MyDatabase>()
    private val pixivAppApi by instance<PixivAppApi>()

    private lateinit var commonViewModel: CommonViewModel
    private lateinit var illustViewModel: IllustViewModel
    private lateinit var illustAdapter: IllustAdapter

    private var type = -1
    private var word = ""
    private var destUserId = "-1"
    private var relatedIllustId: Long = -1
    private var _action: ActionIllust? = null
    private val action get() = _action!!

    private val spanCount: Int
        get() = activity?.getSanCount() ?: 2

    private fun Activity.getSanCount(): Int {
        val itemWidth = resources.getDimensionPixelSize(R.dimen.illust_item_width)
        val count = (getWindowWidth().toFloat() / itemWidth.toFloat()).roundToInt()
        return if (count < 1) 1 else count
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            type = getInt(Keys.PAGE_TYPE, Values.PAGE_TYPE_RECOMMENDED)
            word = getString(Keys.SEARCH_WORD, "")
            destUserId = getString(Keys.USER_ID, "-1")
            relatedIllustId = getLong(Keys.ILLUST_ID, -1)
        }
    }

    override fun onCreateViewModel() {
        super.onCreateViewModel()
        commonViewModel = getViewModel(CommonViewModel(CommonRepositoryImpl(pixivAppApi, db.illustDao())))
        illustViewModel = getViewModel(IllustViewModel(IllustRepositoryImpl(pixivAppApi, db)))
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        illustAdapter = IllustAdapter { isAdd, illust ->
            if (isAdd) {
                commonViewModel.addBookmarkIllust(illust, action.token.auth, Restrict.PUBLIC)
            } else {
                commonViewModel.deleteBookmarkIllust(illust, action.token.auth)
            }
        }
        list.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount, RecyclerView.VERTICAL)
            adapter = illustAdapter.withLoadStateFooterSafe(NetworkLoadStateAdapter(illustAdapter))
        }
        illustAdapter.addLoadStateListener { handleNetworkState(it) }
        lifecycleScope.launchWhenCreated {
            illustViewModel.illusts.collectLatest {
                illustAdapter.submitData(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            illustAdapter.loadStateFlow
                .asMergedLoadStates()
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { list.scrollToPosition(0) }
        }
        swipeRefreshLayout.setOnRefreshListener { illustAdapter.refresh() }
        when (type) {
            Values.PAGE_TYPE_FOLLOWING, Values.PAGE_TYPE_BOOKMARKS -> {
                sharedViewModel.restrict.observe(viewLifecycleOwner, Observer {
                    if (_action != null && action.restrict != it) {
                        action.restrict = it
                        refresh()
                    }
                })
            }
            Values.PAGE_TYPE_RANKING -> {
                sharedViewModel.apply {
                    date.observe(viewLifecycleOwner, Observer {
                        if (_action != null && action.date != it) {
                            action.date = it
                            refresh()
                        }
                    })
                    rankingMode.observe(viewLifecycleOwner, Observer {
                        if (_action != null && action.modeRanking != it) {
                            action.modeRanking = it
                            refresh()
                        }
                    })
                }
            }
            Values.PAGE_TYPE_SEARCH -> {
                sharedViewModel.apply {
                    sort.observe(viewLifecycleOwner, Observer {
                        if (_action != null && action.sort != it) {
                            action.sort = it
                            refresh()
                        }
                    })
                    searchTarget.observe(viewLifecycleOwner, Observer {
                        if (_action != null && action.searchTarget != it) {
                            action.searchTarget = it
                            refresh()
                        }
                    })
                    duration.observe(viewLifecycleOwner, Observer { duration ->
                        if (_action != null && duration != null && action.duration != duration) {
                            action.duration = duration
                            refresh()
                        }
                    })
                    selectedTimeRangeString.observe(viewLifecycleOwner, Observer { dateRange ->
                        if (_action != null) {
                            action.dateRange = dateRange
                            if (action.duration == Duration.CUSTOM) {
                                refresh()
                            }
                        }
                    })
                }
            }
        }
    }

    private fun refresh() {
        illustViewModel.show(action)
        illustAdapter.refresh()
    }

    private fun handleNetworkState(loadStates: CombinedLoadStates) {
        val refresh = loadStates.mediator?.refresh
        val append = loadStates.mediator?.append
        swipeRefreshLayout.isRefreshing = refresh is LoadState.Loading
        progressBarHorizontal.isVisible = append is LoadState.Loading
        val error = when {
            refresh is LoadState.Error -> refresh.error
            append is LoadState.Error -> append.error
            else -> null
        }
        if (error != null) {
            handleException(error)
        }
    }

    private fun handleException(error: Throwable) {
        if (error is HttpException && error.code() == 400) {
            refreshToken()
        }
    }

    override fun onTokenLoaded(token: Token) {
        super.onTokenLoaded(token)
        if (_action == null) {
            _action = ActionIllust(
                type = type,
                token = token,
                query = word,
                date = sharedViewModel.date.value,
                modeRanking = sharedViewModel.rankingModeValue,
                destUserId = destUserId,
                illustId = relatedIllustId
            )
        } else {
            action.token = token
        }
        illustViewModel.show(action)
    }
}