package onlymash.materixiv.ui.module.login

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import okhttp3.HttpUrl
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.isFailed
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.databinding.ActivityLoginBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.extensions.launchUrl
import onlymash.materixiv.extensions.setupTooltipText
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.module.home.MainActivity
import onlymash.materixiv.ui.module.settings.SettingsActivity
import onlymash.materixiv.ui.viewbinding.viewBinding

class LoginActivity : TokenActivity() {

    private val binding by viewBinding(ActivityLoginBinding::inflate)
    private val retryButton get() = binding.retryButton
    private val signInButton get() = binding.signInButton
    private val progressBar get() = binding.progressBar
    private val message get() = binding.message

    private lateinit var loginViewModel: LoginViewModel

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        loginViewModel = getViewModel()
        loginViewModel.code.observe(this) { code ->
            if (code != null) {
                fetchToken(code, loginViewModel.codeVerifier)
            }
        }
        signInButton.setOnClickListener {
            attemptSignIn()
        }
        retryButton.setOnClickListener {
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
        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addEncodedPathSegments("web/v1/login")
            .addQueryParameter("code_challenge", loginViewModel.codeChallenge)
            .addQueryParameter("code_challenge_method", "S256")
            .addQueryParameter("client", "pixiv-android")
            .build()
        launchUrl(httpUrl.toString())
    }

    override val isLoginActivity: Boolean
        get() = true

    override fun onTokenLoaded(token: Token) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onLoginStateChange(state: NetworkState?) {
        retryButton.isVisible = state.isFailed()
        progressBar.isVisible = state.isRunning()
        message.text = state?.msg
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data ?: return
        if (uri.scheme == "pixiv" && uri.host == "account") {
            val code = uri.getQueryParameter("code") ?: return
            loginViewModel.updateCode(code)
        }
    }
}