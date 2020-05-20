package onlymash.materixiv.ui.module.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.databinding.ActivityUserDetailBinding
import onlymash.materixiv.ui.viewbinding.viewBinding

class UserDetailActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, userId: String) {
            context.startActivity(Intent(context, UserDetailActivity::class.java).apply {
                putExtra(Keys.USER_ID, userId)
            })
        }
    }

    private val binding by viewBinding(ActivityUserDetailBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            val id = intent?.getStringExtra(Keys.USER_ID) ?: "-1"
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UserDetailFragment.create(id))
                .commit()
        }
    }
}