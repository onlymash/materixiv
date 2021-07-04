package onlymash.materixiv.ui.module.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import okhttp3.HttpUrl
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.isFailed
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.databinding.FragmentLoginBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.extensions.launchUrl
import onlymash.materixiv.extensions.setupTooltipText
import onlymash.materixiv.ui.module.common.TokenFragment
import onlymash.materixiv.ui.module.home.MainActivity
import onlymash.materixiv.ui.module.settings.SettingsActivity


class LoginFragment : TokenFragment<FragmentLoginBinding>() {

    private val signInButton get() = binding.signInButton
    private val retry get() = binding.retryButton
    private val message get() = binding.message
    private val progressBar get() = binding.progressBar
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onCreateViewModel() {
        super.onCreateViewModel()
        loginViewModel = requireActivity().getViewModel()
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        loginViewModel.code.observe(viewLifecycleOwner, { code ->
            if (code != null) {
                fetchToken(code, loginViewModel.codeVerifier)
            }
        })
        signInButton.setOnClickListener {
            attemptSignIn()
        }
        retry.setOnClickListener {
            val code = loginViewModel.code.value
            if (code != null) {
                fetchToken(code, loginViewModel.codeVerifier)
            }
        }
        binding.settingsButton.apply {
            setOnClickListener {
                startActivity(Intent(context, SettingsActivity::class.java))
            }
            setupTooltipText()
        }
    }

    private fun attemptSignIn() {
        val context = context ?: return
        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addEncodedPathSegments("web/v1/login")
            .addQueryParameter("code_challenge", loginViewModel.codeChallenge)
            .addQueryParameter("code_challenge_method", "S256")
            .addQueryParameter("client", "pixiv-android")
            .build()
        context.launchUrl(httpUrl.toString())
    }

    override fun onTokenLoaded(token: Token) {
        val activity = activity ?: return
        startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }

    override fun onLoginStateChange(state: NetworkState?) {
        retry.isVisible = state.isFailed()
        progressBar.isVisible = state.isRunning()
        message.text = state?.msg
    }

    override fun onRefreshStateChange(state: NetworkState?) {

    }
}