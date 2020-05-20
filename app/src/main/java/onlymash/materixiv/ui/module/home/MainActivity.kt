package onlymash.materixiv.ui.module.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import onlymash.materixiv.R
import onlymash.materixiv.data.db.dao.TokenDao
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.databinding.ActivityMainBinding
import onlymash.materixiv.extensions.findNavController
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.base.KodeinActivity
import onlymash.materixiv.ui.module.download.DownloadsActivity
import onlymash.materixiv.ui.module.settings.SettingsActivity
import onlymash.materixiv.ui.module.user.UserDetailActivity
import onlymash.materixiv.ui.viewbinding.viewBinding
import org.kodein.di.erased.instance

class MainActivity : KodeinActivity() {

    private val tokenDao by instance<TokenDao>()

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val bottomNavView get() = binding.bottomNavView
    private val drawerLayout get() = binding.drawerLayout
    private val leftNavView get() = binding.leftNavView
    private lateinit var headerView: View
    private lateinit var homeViewModel: HomeViewModel
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        homeViewModel = getViewModel(HomeViewModel(tokenDao))
        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavView.setupWithNavController(navController)
        leftNavView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_downloads -> startActivity(Intent(this, DownloadsActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            }
            true
        }
        headerView = leftNavView.getHeaderView(0)
        headerView.setOnClickListener {
            userId?.let { id ->
                UserDetailActivity.start(this, id)
            }
        }
        headerView.findViewById<View>(R.id.logout).setOnClickListener {
            showLogoutDialog()
        }
        homeViewModel.tokens.observe(this, Observer { tokens ->
            if (!tokens.isNullOrEmpty()) {
                bindDrawerHeader(tokens[0])
            }
        })
        homeViewModel.loadTokens()
    }

    private fun bindDrawerHeader(token: Token) {
        userId = token.userId
        val avatar = headerView.findViewById<ShapeableImageView>(R.id.avatar)
        val name = headerView.findViewById<MaterialTextView>(R.id.name)
        val email = headerView.findViewById<MaterialTextView>(R.id.email)
        name.text = token.data.profile.name
        email.text = token.data.profile.mailAddress
        GlideApp.with(this)
            .load(token.data.profile.profileImageUrls.px170x170)
            .placeholder(ContextCompat.getDrawable(this, R.drawable.placeholder_avatar))
            .into(avatar)
    }

    private fun showLogoutDialog() {
        if (isFinishing) {
            return
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.home_logout)
            .setMessage(R.string.home_logout_tip)
            .setPositiveButton(R.string.dialog_yes) { _, _  ->
                homeViewModel.deleteAllTokens()
            }
            .setNegativeButton(R.string.dialog_no, null)
            .create()
            .show()
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START, true)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }
}