package onlymash.materixiv.ui.module.comment

import android.os.Bundle
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.databinding.ActivityCommentBinding
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.viewbinding.viewBinding

class CommentActivity : TokenActivity() {

    private val binding by viewBinding(ActivityCommentBinding::inflate)

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
    }

    override fun onTokenLoaded(token: Token) {

    }

    override fun onLoginStateChange(state: NetworkState?) {

    }

    override fun onRefreshStateChange(state: NetworkState?) {

    }
}