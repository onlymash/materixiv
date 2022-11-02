package onlymash.materixiv.ui.module.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import onlymash.materixiv.R
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.databinding.ActivityMainBinding
import onlymash.materixiv.extensions.findNavController
import onlymash.materixiv.extensions.launchUrl
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.module.download.DownloadsActivity
import onlymash.materixiv.ui.module.novel.NovelBookmarksActivity
import onlymash.materixiv.ui.module.settings.SettingsActivity
import onlymash.materixiv.ui.module.user.UserDetailActivity
import onlymash.materixiv.ui.viewbinding.viewBinding

class MainActivity : TokenActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val bottomNavView get() = binding.bottomNavView
    private val drawerLayout get() = binding.drawerLayout
    private val leftNavView get() = binding.leftNavView
    private lateinit var headerView: View
    private var userId: String? = null
    private val bottomNavItemReselectListeners: MutableList<BottomNavItemReselectedListener> = mutableListOf()
    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }
    private val requestNotificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {

    }

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavView.apply {
            setupWithNavController(navController)
            setOnItemReselectedListener { menuItem ->
                bottomNavItemReselectListeners.forEach { listener ->
                    listener.onReselectedItem(menuItem.itemId)
                }
            }
        }
        leftNavView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_bookmarks -> startActivity(Intent(this, NovelBookmarksActivity::class.java))
                R.id.nav_downloads -> startActivity(Intent(this, DownloadsActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_link -> launchUrl("https://t.me/materixiv")
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
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) { }
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) { }
            override fun onDrawerClosed(drawerView: View) {
                onBackPressedCallback.isEnabled = false
            }
            override fun onDrawerOpened(drawerView: View) {
                onBackPressedCallback.isEnabled = true
            }
        })
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onTokenLoaded(token: Token) {
        bindDrawerHeader(token)
    }

    private fun bindDrawerHeader(token: Token) {
        userId = token.userId
        val avatar = headerView.findViewById<ShapeableImageView>(R.id.avatar)
        val name = headerView.findViewById<MaterialTextView>(R.id.name)
        val email = headerView.findViewById<MaterialTextView>(R.id.email)
        name.text = token.data.user.name
        email.text = token.data.user.mailAddress
        GlideApp.with(this)
            .load(token.data.user.profileImageUrls.px170x170)
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
                deleteAllTokens()
            }
            .setNegativeButton(R.string.dialog_no, null)
            .create()
            .show()
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START, true)
    }

    interface BottomNavItemReselectedListener {
        fun onReselectedItem(itemId: Int)
    }

    fun addBottomNavItemReselectedListener(listener: BottomNavItemReselectedListener) {
        bottomNavItemReselectListeners.add(listener)
    }

    fun removeBottomNavItemReselectedListener(listener: BottomNavItemReselectedListener) {
        bottomNavItemReselectListeners.remove(listener)
    }
}