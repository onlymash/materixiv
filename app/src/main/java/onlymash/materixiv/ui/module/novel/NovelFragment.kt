package onlymash.materixiv.ui.module.novel

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.isRefreshToken
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.data.repository.novel.NovelRepositoryImpl
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.SharedViewModelFragment
import org.kodein.di.instance

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
        novelAdapter = NovelAdapter(
            retryCallback = {
                novelViewModel.retry()
            }
        )
        list.apply {
            updatePadding(left = 0, right = 0)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = novelAdapter
        }
        novelViewModel.novels.observe(viewLifecycleOwner, Observer { novels ->
            if (novels != null) {
                novelAdapter.submitList(novels)
                progressBarCircular.isVisible = novels.size == 0
            }
        })
        novelViewModel.refreshState.observe(viewLifecycleOwner, Observer {
            if (!it.isRunning()) {
                swipeRefreshLayout.isRefreshing = false
            }
        })
        novelViewModel.networkState.observe(viewLifecycleOwner, Observer {
            handleNetworkState(it)
        })
        swipeRefreshLayout.setOnRefreshListener {
            novelViewModel.refresh()
        }
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
        novelViewModel.refresh()
    }

    private fun handleNetworkState(state: NetworkState?) {
        if (!state.isRunning()) {
            swipeRefreshLayout.isRefreshing = false
        }
        novelAdapter.setNetworkState(state)
        val isRunning = state.isRunning()
        val isRefreshToken = if (isRunning) false else state.isRefreshToken()
        progressBarCircular.isVisible = (isRefreshToken || isRunning) && novelAdapter.itemCount == 0
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