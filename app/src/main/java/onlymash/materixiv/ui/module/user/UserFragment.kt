package onlymash.materixiv.ui.module.user

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.MyDatabase
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.db.entity.UserCache
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.user.UserRepositoryImpl
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.SharedViewModelFragment
import org.kodein.di.instance
import retrofit2.HttpException

class UserFragment : SharedViewModelFragment() {

    companion object {
        fun create(type: Int, word: String? = null, userId: String? = null): UserFragment {
            return UserFragment().apply {
                arguments = Bundle().apply {
                    putInt(Keys.PAGE_TYPE, type)
                    putString(Keys.SEARCH_WORD, word)
                    putString(Keys.USER_ID, userId)
                }
            }
        }
    }

    private val pixivAppApi by instance<PixivAppApi>()
    private val db by instance<MyDatabase>()

    private lateinit var userAdapter: UserAdapter
    private lateinit var userViewModel: UserViewModel
    private lateinit var commonViewModel: CommonViewModel

    private var type: Int = Values.PAGE_TYPE_RECOMMENDED
    private var word: String = ""
    private var userId: String = ""
    private var _action: ActionUser? = null
    private val action get() = _action!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            type = getInt(Keys.PAGE_TYPE, Values.PAGE_TYPE_RECOMMENDED)
            word = getString(Keys.SEARCH_WORD, "")
            userId = getString(Keys.USER_ID, "")
        }
    }

    override fun onCreateViewModel() {
        super.onCreateViewModel()
        userViewModel = getViewModel(UserViewModel(UserRepositoryImpl(pixivAppApi, db)))
        commonViewModel = getViewModel(CommonViewModel(CommonRepositoryImpl(pixivAppApi)))
    }

    private fun followUser(userCache: UserCache, restrict: Restrict) {
        lifecycleScope.launch {
            val isFollowed = userCache.userPreview.user.isFollowed
            val success = if (isFollowed) {
                commonViewModel.deleteFollowUser(action.auth, userCache.id)
            } else {
                commonViewModel.addFollowUser(action.auth, userCache.id, restrict)
            }
            if (success) {
                userCache.userPreview.user.isFollowed = !isFollowed
                db.userDao().update(userCache)
            }
        }
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        userAdapter = UserAdapter(this::followUser)
        list.apply {
            updatePadding(left = 0, right = 0)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = userAdapter
        }
        userAdapter.addLoadStateListener { handleNetworkState(it) }
        userViewModel.users.observe(viewLifecycleOwner, Observer {
            userAdapter.submitData(lifecycle, it)
        })
        swipeRefreshLayout.setOnRefreshListener { userAdapter.refresh() }
        retryButton.setOnClickListener { userAdapter.retry() }
        if (type == Values.PAGE_TYPE_FOLLOWING) {
            sharedViewModel.restrict.observe(viewLifecycleOwner, Observer {
                if (_action != null) {
                    action.restrict = it
                    refresh()
                }
            })
        }
    }

    private fun refresh() {
        userViewModel.show(action)
        userAdapter.refresh()
    }

    private fun handleNetworkState(loadStates: CombinedLoadStates) {
        val refresh = loadStates.refresh
        val append = loadStates.append
        val isEmptyList = userAdapter.itemCount == 0
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
            _action = ActionUser(
                type = type,
                auth = token.auth,
                tokenUid = token.uid,
                userId = token.userId,
                word = word
            )
            if (userId.isNotEmpty()) {
                action.userId = userId
            }
        } else {
            action.auth = token.auth
        }
        userViewModel.show(action)
    }
}