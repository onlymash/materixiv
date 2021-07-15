package onlymash.materixiv.ui.module.common

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import onlymash.materixiv.data.api.PixivOauthApi
import onlymash.materixiv.data.db.dao.TokenDao
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.token.TokenRepositoryImpl
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.base.ViewModelDialog
import org.kodein.di.instance

abstract class TokenDialog<T: ViewBinding> : ViewModelDialog<T>() {

    private val tokenDao by instance<TokenDao>()
    private val pixivOauthApi by instance<PixivOauthApi>()

    private lateinit var tokenViewModel: TokenViewModel

    override fun onCreateViewModel() {
        tokenViewModel = requireActivity().getViewModel(TokenViewModel(TokenRepositoryImpl(pixivOauthApi, tokenDao)))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBaseViewCreated(view, savedInstanceState)
        tokenViewModel.tokens.observe(viewLifecycleOwner, { tokens ->
            if (!tokens.isNullOrEmpty()) {
                onTokenLoaded(tokens[0])
            }
        })
        tokenViewModel.loginState.observe(viewLifecycleOwner, {
            onLoginStateChange(it)
        })
        tokenViewModel.refreshState.observe(viewLifecycleOwner, {
            onRefreshStateChange(it)
        })
    }

    abstract fun onBaseViewCreated(view: View, savedInstanceState: Bundle?)

    abstract fun onTokenLoaded(token: Token)

    open fun onLoginStateChange(state: NetworkState?) = Unit

    open fun onRefreshStateChange(state: NetworkState?) = Unit

    protected fun fetchToken(code: String, codeVerifier: String) {
        tokenViewModel.fetchToken(code, codeVerifier)
    }

    protected fun refreshToken() {
        tokenViewModel.refresh()
    }
}