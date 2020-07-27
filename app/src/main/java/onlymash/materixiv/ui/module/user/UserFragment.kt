package onlymash.materixiv.ui.module.user

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.isRefreshToken
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.data.repository.user.UserRepositoryImpl
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.SharedViewModelFragment
import org.kodein.di.instance

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
        userViewModel = getViewModel(UserViewModel(UserRepositoryImpl(pixivAppApi)))
        commonViewModel = getViewModel(CommonViewModel(CommonRepositoryImpl(pixivAppApi)))
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        userAdapter = UserAdapter(
            followCallback = { userId, isFollowed, restrict ->
                userViewModel.updateFollowState(userId, isFollowed)
                if (isFollowed) {
                    commonViewModel.addFollowUser(action.auth, userId, restrict)
                } else {
                    commonViewModel.deleteFollowUser(action.auth, userId)
                }
            },
            retryCallback = {
                userViewModel.retry()
            }
        )
        list.apply {
            updatePadding(left = 0, right = 0)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = userAdapter
        }
        userViewModel.users.observe(viewLifecycleOwner, Observer { userPreviews ->
            if (userPreviews != null) {
                userAdapter.submitList(userPreviews)
                progressBarCircular.isVisible = userPreviews.size == 0
            }
        })
        userViewModel.refreshState.observe(viewLifecycleOwner, Observer {
            if (!it.isRunning()) {
                swipeRefreshLayout.isRefreshing = false
            }
        })
        userViewModel.networkState.observe(viewLifecycleOwner, Observer {
            handleNetworkState(it)
        })
        swipeRefreshLayout.setOnRefreshListener {
            userViewModel.refresh()
        }
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
        userViewModel.refresh()
    }

    private fun handleNetworkState(state: NetworkState?) {
        if (!state.isRunning()) {
            swipeRefreshLayout.isRefreshing = false
        }
        userAdapter.setNetworkState(state)
        val isRunning = state.isRunning()
        val isRefreshToken = if (isRunning) false else state.isRefreshToken()
        progressBarCircular.isVisible = (isRefreshToken || isRunning) && userAdapter.itemCount == 0
        progressBarHorizontal.isVisible = isRunning
        if (isRefreshToken) {
            val token = sharedViewModel.token.value
            if (token != null) {
                refreshToken(
                    uid = token.uid,
                    refreshToken = token.data.refreshToken,
                    deviceToken = token.data.deviceToken
                )
            }
        }
    }

    override fun onTokenLoaded(token: Token) {
        super.onTokenLoaded(token)
        if (_action == null) {
            _action = ActionUser(
                type = type,
                auth = token.auth,
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