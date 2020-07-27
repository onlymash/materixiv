package onlymash.materixiv.ui.module.illust

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.dao.IllustDao
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.detail.IllustDeatilRepositoryImpl
import onlymash.materixiv.databinding.ActivityIllustDeatilBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.extensions.isNightTheme
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.viewbinding.viewBinding
import org.kodein.di.instance

class IllustDeatilActivity : TokenActivity() {

    companion object {
        private const val QUERY_KEY = "query"
        private const val POSITION_KEY = "position"
        private const val ILLUST_ID_KEY = "illust_id"
        fun start(context: Context, query: String, position: Int) {
            context.startActivity(Intent(context, IllustDeatilActivity::class.java).apply {
                putExtra(QUERY_KEY, query)
                putExtra(POSITION_KEY, position)
            })
        }
        fun start(context: Context, illustId: Long) {
            context.startActivity(Intent(context, IllustDeatilActivity::class.java).apply {
                putExtra(ILLUST_ID_KEY, illustId)
            })
        }

    }

    private val binding by viewBinding(ActivityIllustDeatilBinding::inflate)
    private val illustDao by instance<IllustDao>()
    private val pixivAppApi by instance<PixivAppApi>()

    private val fragmentPager get() = binding.detailFragmentPager
    private lateinit var adapter: IllustDetailPagerAdapter
    private lateinit var pagerViewModel: IllustDetailPagerViewModel
    private lateinit var sharedViewModel: IllustDetailSharedViewModel

    private var query: String? = null
    private var illustId = -1L

    private fun Uri.isValid(): Boolean {
        return scheme == "https" && host == Values.HOST_WEB
    }

    private fun Uri.getIllustId(): Long {
        return path?.replace("/artworks/", "")?.toLong() ?: 0
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            pagerViewModel.position = position
        }
    }

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (!resources.configuration.isNightTheme() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        window.decorView.systemUiVisibility = flags
        sharedViewModel = getViewModel()
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            sharedViewModel.updateTopSize(insets.systemWindowInsetTop)
            insets
        }
        adapter = IllustDetailPagerAdapter(supportFragmentManager, lifecycle)
        fragmentPager.offscreenPageLimit = 1
        fragmentPager.adapter = adapter
        pagerViewModel = getViewModel(IllustDetailPagerViewModel(
            IllustDeatilRepositoryImpl(
                pixivAppApi,
                illustDao
            )
        ))
        intent?.apply {
            query = getStringExtra(QUERY_KEY)
            if (savedInstanceState == null) {
                pagerViewModel.position = getIntExtra(POSITION_KEY, 0)
            }
            if (query == null) {
                illustId = getLongExtra(ILLUST_ID_KEY, -1L)
                if (illustId < 0) {
                    val uri = data
                    if (uri != null && uri.isValid()) {
                        illustId = uri.getIllustId()
                    }
                }
                query = "id:$illustId"
            }
        }
        pagerViewModel.illusts.observe(this, Observer { illusts ->
            val token = pagerViewModel.token
            if (illusts != null && illusts.isNotEmpty() && token != null) {
                adapter.submitData(illusts, token.uid, token.auth, query ?: "") {
                    if (savedInstanceState == null && pagerViewModel.position < illusts.size) {
                        fragmentPager.setCurrentItem(pagerViewModel.position, false)
                    }
                    binding.progressBar.isVisible = false
                }
            }
        })
        if (illustId > 0) {
            pagerViewModel.isSuccess.observe(this, Observer { success ->
                if (!success) {
                    binding.progressBar.isVisible = false
                    binding.retryButton.isVisible = true
                }
            })
            binding.retryButton.setOnClickListener {
                binding.retryButton.isVisible = false
                binding.progressBar.isVisible = true
                fetchIllustFromNet()
            }
        }
        fragmentPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onTokenLoaded(token: Token) {
        pagerViewModel.token = token
        val query = query ?: return
        pagerViewModel.load(tokenUid = token.uid, query = query, initialPosition = pagerViewModel.position)
        if (illustId > 0) {
            fetchIllustFromNet()
        }
    }

    private fun fetchIllustFromNet() {
        pagerViewModel.fetch(illustId = illustId)
    }

    override fun onLoginStateChange(state: NetworkState?) {

    }

    override fun onRefreshStateChange(state: NetworkState?) {

    }
}