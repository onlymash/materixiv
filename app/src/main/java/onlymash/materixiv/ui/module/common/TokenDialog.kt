package onlymash.materixiv.ui.module.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import onlymash.materixiv.data.api.PixivOauthApi
import onlymash.materixiv.data.db.dao.TokenDao
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.token.TokenRepositoryImpl
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.base.ViewModelDialog
import onlymash.materixiv.ui.module.login.LoginActivity
import org.kodein.di.erased.instance

abstract class TokenDialog<T: ViewBinding> : ViewModelDialog<T>() {

    private val tokenDao by instance<TokenDao>()
    private val pixivOauthApi by instance<PixivOauthApi>()

    private lateinit var tokenViewModel: TokenViewModel

    override fun onCreateViewModel() {
        tokenViewModel = getViewModel(
            TokenViewModel(
                TokenRepositoryImpl(pixivOauthApi, tokenDao)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBaseViewCreated(view, savedInstanceState)
        tokenViewModel.load().observe(viewLifecycleOwner, Observer { tokens ->
            if (tokens.isNullOrEmpty()) {
                toLoginPage()
            } else {
                val token = tokens[0]
                if (token.isExpired) {
                    refreshToken(token.uid, token.data.refreshToken, token.data.deviceToken)
                } else {
                    onTokenLoaded(token)
                }
            }
        })
        tokenViewModel.loginState.observe(viewLifecycleOwner, Observer {
            onLoginStateChange(it)
        })
        tokenViewModel.refreshState.observe(viewLifecycleOwner, Observer {
            onRefreshStateChange(it)
        })
    }

    abstract fun onBaseViewCreated(view: View, savedInstanceState: Bundle?)

    private fun toLoginPage() {
        val activity = activity ?: return
        startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
    }

    abstract fun onTokenLoaded(token: Token)

    abstract fun onLoginStateChange(state: NetworkState?)

    abstract fun onRefreshStateChange(state: NetworkState?)

    protected fun login(username: String, password: String) {
        tokenViewModel.login(username, password)
    }

    protected fun refreshToken(uid: Long, refreshToken: String, deviceToken: String) {
        tokenViewModel.refresh(uid, refreshToken, deviceToken)
    }
}