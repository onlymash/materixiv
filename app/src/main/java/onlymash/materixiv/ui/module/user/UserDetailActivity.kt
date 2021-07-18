package onlymash.materixiv.ui.module.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.databinding.ActivityUserDetailBinding
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.viewbinding.viewBinding

class UserDetailActivity : TokenActivity() {

    companion object {
        fun start(
            context: Context,
            userId: String,
            targetPage: Int = UserDetailFragment.TARGET_PAGE_ILLUST
        ) {
            context.startActivity(Intent(context, UserDetailActivity::class.java).apply {
                putExtra(Keys.USER_ID, userId)
                putExtra(UserDetailFragment.TARGET_PAGE_KEY, targetPage)
            })
        }
    }

    private val binding by viewBinding(ActivityUserDetailBinding::inflate)

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        if (savedInstanceState == null) {
            var id = "-1"
            intent?.apply {
                id = getStringExtra(Keys.USER_ID) ?:
                data?.path?.replace("/users/", "")?.substringBefore("/") ?: "-1"
            }
            val targetPage = intent?.getIntExtra(UserDetailFragment.TARGET_PAGE_KEY, UserDetailFragment.TARGET_PAGE_ILLUST) ?: UserDetailFragment.TARGET_PAGE_ILLUST
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UserDetailFragment.create(id, targetPage))
                .commit()
        }
    }
}