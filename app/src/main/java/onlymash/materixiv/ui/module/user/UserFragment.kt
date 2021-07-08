package onlymash.materixiv.ui.module.user

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
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
import onlymash.materixiv.extensions.asMergedLoadStates
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.NetworkLoadStateAdapter
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
            adapter = userAdapter.withLoadStateFooterSafe(NetworkLoadStateAdapter(userAdapter))
        }
        userAdapter.addLoadStateListener { handleNetworkState(it) }
        lifecycleScope.launchWhenCreated {
            userViewModel.users.collectLatest {
                userAdapter.submitData(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            userAdapter.loadStateFlow
                .asMergedLoadStates()
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { list.scrollToPosition(0) }
        }
        swipeRefreshLayout.setOnRefreshListener { userAdapter.refresh() }
        if (type == Values.PAGE_TYPE_FOLLOWING) {
            sharedViewModel.restrict.observe(viewLifecycleOwner, {
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