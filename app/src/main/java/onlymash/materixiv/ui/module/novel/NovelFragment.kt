package onlymash.materixiv.ui.module.novel

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.action.Duration
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.novel.NovelRepositoryImpl
import onlymash.materixiv.extensions.asMergedLoadStates
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.NetworkLoadStateAdapter
import onlymash.materixiv.ui.module.common.SharedViewModelFragment
import onlymash.materixiv.ui.paging.withLoadStateFooterSafe
import org.kodein.di.instance
import retrofit2.HttpException

class NovelFragment : SharedViewModelFragment() {

    companion object {
        fun create(type: Int, word: String? = null, userId: String? = null): NovelFragment {
            return NovelFragment().apply {
                arguments = Bundle().apply {
                    putInt(Keys.PAGE_TYPE, type)
                    putString(Keys.SEARCH_WORD, word)
                    putString(Keys.USER_ID, userId)
                }
            }
        }
    }

    private val pixivAppApi by instance<PixivAppApi>()

    private lateinit var novelAdapter: NovelAdapter
    private lateinit var novelViewModel: NovelViewModel
    private lateinit var commonViewModel: CommonViewModel

    private var type: Int = Values.PAGE_TYPE_RECOMMENDED
    private var word: String = ""
    private var destUserId = "-1"
    private var _action: ActionNovel? = null
    private val action get() = _action!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            type = getInt(Keys.PAGE_TYPE, Values.PAGE_TYPE_RECOMMENDED)
            word = getString(Keys.SEARCH_WORD, "")
            destUserId = getString(Keys.USER_ID, "-1")
        }
    }

    override fun onCreateViewModel() {
        super.onCreateViewModel()
        novelViewModel = getViewModel(NovelViewModel(NovelRepositoryImpl(pixivAppApi)))
        commonViewModel = getViewModel(CommonViewModel(CommonRepositoryImpl(pixivAppApi)))
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        novelAdapter = NovelAdapter()
        list.apply {
            updatePadding(left = 0, right = 0)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = novelAdapter.withLoadStateFooterSafe(NetworkLoadStateAdapter(novelAdapter))
        }
        novelAdapter.addLoadStateListener { handleNetworkState(it) }
        lifecycleScope.launchWhenCreated {
            novelViewModel.novels.collectLatest {
                novelAdapter.submitData(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            novelAdapter.loadStateFlow
                .asMergedLoadStates()
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { list.scrollToPosition(0) }
        }
        swipeRefreshLayout.setOnRefreshListener { novelAdapter.refresh() }
        when (type) {
            Values.PAGE_TYPE_FOLLOWING -> {
                sharedViewModel.restrict.observe(viewLifecycleOwner) {
                    if (_action != null) {
                        action.restrict = it
                        refresh()
                    }
                }
            }
            Values.PAGE_TYPE_RANKING -> {
                sharedViewModel.rankingMode.observe(viewLifecycleOwner) {
                    if (_action != null) {
                        action.modeRanking = it
                        refresh()
                    }
                }
                sharedViewModel.date.observe(viewLifecycleOwner) {
                    if (_action != null) {
                        action.date = it
                        refresh()
                    }
                }
            }
            Values.PAGE_TYPE_SEARCH -> {
                sharedViewModel.apply {
                    sort.observe(viewLifecycleOwner) {
                        if (_action != null && action.sort != it) {
                            action.sort = it
                            refresh()
                        }
                    }
                    searchTarget.observe(viewLifecycleOwner) {
                        if (_action != null && action.searchTarget != it) {
                            action.searchTarget = it
                            refresh()
                        }
                    }
                    duration.observe(viewLifecycleOwner) { duration ->
                        if (_action != null && duration != null && action.duration != duration) {
                            action.duration = duration
                            refresh()
                        }
                    }
                    selectedTimeRangeString.observe(viewLifecycleOwner) { dateRange ->
                        if (_action != null) {
                            action.dateRange = dateRange
                            if (action.duration == Duration.CUSTOM) {
                                refresh()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun refresh() {
        novelViewModel.show(action)
        novelAdapter.refresh()
    }

    private fun handleNetworkState(loadStates: CombinedLoadStates) {
        val refresh = loadStates.source.refresh
        val append = loadStates.source.append
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
            _action = ActionNovel(
                type = type,
                token = token,
                word = word,
                date = sharedViewModel.date.value,
                modeRanking = sharedViewModel.rankingModeValue,
                destUserId = destUserId
            )
        } else {
            action.token = token
        }
        novelViewModel.show(action)
    }
}