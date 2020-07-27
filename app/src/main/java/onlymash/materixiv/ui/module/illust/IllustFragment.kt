package onlymash.materixiv.ui.module.illust

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.MyDatabase
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.illust.IllustRepositoryImpl
import onlymash.materixiv.data.repository.isRefreshToken
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.extensions.getWindowWidth
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.SharedViewModelFragment
import org.kodein.di.instance
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
        illustViewModel = getViewModel(IllustViewModel(IllustRepositoryImpl(db, pixivAppApi)))
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        illustAdapter = IllustAdapter(
            bookmarkCallback = { isAdd, illust ->
                if (isAdd) {
                    commonViewModel.addBookmarkIllust(illust, action.token.auth, Restrict.PUBLIC)
                } else {
                    commonViewModel.deleteBookmarkIllust(illust, action.token.auth)
                }
            },
            retryCallback = {
                illustViewModel.retry()
            }
        )
        list.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount, RecyclerView.VERTICAL)
            adapter = illustAdapter
        }
        illustViewModel.illusts.observe(viewLifecycleOwner, Observer { illusts ->
            if (illusts != null) {
                illustAdapter.submitList(illusts)
                progressBarCircular.isVisible = illusts.size == 0
            }
        })
        illustViewModel.refreshState.observe(viewLifecycleOwner, Observer {
            if (!it.isRunning()) {
                swipeRefreshLayout.isRefreshing = false
            }
        })
        illustViewModel.networkState.observe(viewLifecycleOwner, Observer {
            handleNetworkState(it)
        })
        swipeRefreshLayout.setOnRefreshListener {
            illustViewModel.refresh()
        }
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
                    duration.observe(viewLifecycleOwner, Observer {
                        if (_action != null && action.duration != it) {
                            action.duration = it
                            refresh()
                        }
                    })
                }
            }
        }
    }

    private fun refresh() {
        swipeRefreshLayout.isRefreshing = true
        illustViewModel.show(action)
        illustViewModel.refresh()
    }

    private fun handleNetworkState(state: NetworkState?) {
        illustAdapter.setNetworkState(state)
        val isRunning = state.isRunning()
        val isRefreshToken = if (isRunning) false else state.isRefreshToken()
        progressBarCircular.isVisible = (isRefreshToken || isRunning) && illustAdapter.itemCount == 0
        progressBarHorizontal.isVisible = isRunning
        if (isRefreshToken) {
            refreshToken(
                uid = action.token.uid,
                refreshToken = action.token.data.refreshToken,
                deviceToken = action.token.data.deviceToken
            )
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