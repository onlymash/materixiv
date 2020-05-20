package onlymash.materixiv.ui.module.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import onlymash.materixiv.R
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.isFailed
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.databinding.FragmentLoginBinding
import onlymash.materixiv.extensions.launchUrl
import onlymash.materixiv.extensions.setupTooltipText
import onlymash.materixiv.ui.module.common.TokenFragment
import onlymash.materixiv.ui.module.home.MainActivity
import onlymash.materixiv.ui.module.settings.SettingsActivity


class LoginFragment : TokenFragment<FragmentLoginBinding>() {

    private val signInButton get() = binding.signInButton
    private val processBar get() = binding.progressCircular.progressBarCircular

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        signInButton.setOnClickListener {
            attemptSignIn()
        }
        binding.settingsButton.apply {
            setOnClickListener {
                startActivity(Intent(context, SettingsActivity::class.java))
            }
            setupTooltipText()
        }
        binding.register.setOnClickListener { 
            context?.launchUrl("https://accounts.pixiv.net/signup")
        }
    }

    private fun attemptSignIn() {
        val username = binding.usernameEdit.text?.toString()?.trim()
        val password = binding.passwordEdit.text?.toString()
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            Snackbar.make(requireView(), getString(R.string.login_msg_tip_empty), Snackbar.LENGTH_LONG).show()
            return
        }
        login(username, password)
    }

    override fun onTokenLoaded(token: Token) {
        val activity = activity ?: return
        startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }

    override fun onLoginStateChange(state: NetworkState?) {
        bindState(state)
    }

    override fun onRefreshStateChange(state: NetworkState?) {
        bindState(state)
    }

    private fun bindState(state: NetworkState?) {
        if (state.isRunning()) {
            processBar.isVisible = true
            signInButton.isVisible = false
        } else {
            processBar.isVisible = false
            signInButton.isVisible = true
            if (state.isFailed()) {
                binding.errorMsg.apply {
                    isVisible = true
                    text = state?.msg
                }
            } else {
                binding.errorMsg.apply {
                    isVisible = false
                    text = null
                }
            }
        }
    }
}