package onlymash.materixiv.ui.module.login

import android.content.Intent
import android.os.Bundle
import onlymash.materixiv.R
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.base.BaseActivity

class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = getViewModel()
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