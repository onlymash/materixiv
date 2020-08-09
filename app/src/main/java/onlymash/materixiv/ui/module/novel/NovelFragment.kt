package onlymash.materixiv.ui.module.novel

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.novel.NovelRepositoryImpl
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.SharedViewModelFragment
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
            adapter = novelAdapter
        }
        novelAdapter.addLoadStateListener { handleNetworkState(it) }
        novelViewModel.novels.observe(viewLifecycleOwner, Observer {
            novelAdapter.submitData(lifecycle, it)
        })
        swipeRefreshLayout.setOnRefreshListener { novelAdapter.refresh() }
        retryButton.setOnClickListener { novelAdapter.retry() }
        when (type) {
            Values.PAGE_TYPE_FOLLOWING -> {
                sharedViewModel.restrict.observe(viewLifecycleOwner, Observer {
                    if (_action != null) {
                        action.restrict = it
                        refresh()
                    }
                })
            }
            Values.PAGE_TYPE_RANKING -> {
                sharedViewModel.rankingMode.observe(viewLifecycleOwner, Observer {
                    if (_action != null) {
                        action.modeRanking = it
                        refresh()
                    }
                })
                sharedViewModel.date.observe(viewLifecycleOwner, Observer {
                    if (_action != null) {
                        action.date = it
                        refresh()
                    }
                })
            }
        }
    }

    private fun refresh() {
        novelViewModel.show(action)
        novelAdapter.refresh()
    }

    private fun handleNetworkState(loadStates: CombinedLoadStates) {
        val refresh = loadStates.refresh
        val append = loadStates.append
        val isEmptyList = novelAdapter.itemCount == 0
        if (refresh is LoadState.Error || append is LoadState.Error) {
            val error = if (refresh is LoadState.Error) {
                refresh.error
            } else {
                (append as LoadState.Error).error
            }
            handleException(error, isEmptyList)
        } else {
            retryButton.isVisible = false
            swipeRefreshLayout.isVisible = true
        }
        val isRefreshing = refresh is LoadState.Loading
        setSwipeRefreshing(isRefreshing && !isEmptyList)
        progressBarCircular.isVisible = isRefreshing && isEmptyList
        progressBarHorizontal.isVisible = append is LoadState.Loading
    }

    private fun setSwipeRefreshing(isRefreshing: Boolean) {
        if (swipeRefreshLayout.isRefreshing != isRefreshing) {
            swipeRefreshLayout.isRefreshing = isRefreshing
        }
    }

    private fun handleException(error: Throwable, isEmptyList: Boolean) {
        val token = sharedViewModel.token.value
        if (token != null && error is HttpException && error.code() == 400) {
            retryButton.isVisible = false
            swipeRefreshLayout.isVisible = true
            setSwipeRefreshing(!isEmptyList)
            progressBarCircular.isVisible = isEmptyList
            refreshToken(
                uid = token.uid,
                refreshToken = token.data.refreshToken,
                deviceToken = token.data.deviceToken
            )
        } else {
            swipeRefreshLayout.isVisible = false
            progressBarCircular.isVisible = false
            retryButton.isVisible = true
        }
    }

    override fun onTokenLoaded(token: Token) {
        super.onTokenLoaded(token)
        if (_action == null) {
            _action = ActionNovel(
                type = type,
                auth = token.auth,
                myUserId = token.userId,
                word = word,
                date = sharedViewModel.date.value,
                modeRanking = sharedViewModel.rankingModeValue,
                destUserId = destUserId
            )
        } else {
            action.auth = token.auth
            action.myUserId = token.userId
        }
        novelViewModel.show(action)
    }
}