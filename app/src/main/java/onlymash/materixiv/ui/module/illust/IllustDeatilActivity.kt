package onlymash.materixiv.ui.module.illust

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.viewpager2.widget.ViewPager2
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.ActionDetail
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.dao.IllustCacheDao
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.detail.IllustDeatilRepositoryImpl
import onlymash.materixiv.databinding.ActivityIllustDeatilBinding
import onlymash.materixiv.extensions.drawSystemBar
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
    private val illustDao by instance<IllustCacheDao>()
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
        return path?.replace("/artworks/", "")?.substringBefore("/")?.toLong() ?: 0L
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            pagerViewModel.position = position
        }
    }

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        window.drawSystemBar(!resources.configuration.isNightTheme())
        sharedViewModel = getViewModel()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            sharedViewModel.updateInsets(insets.getInsets(WindowInsetsCompat.Type.systemBars()))
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
        adapter.addLoadStateListener {
            binding.progressBar.isVisible = it.refresh is LoadState.Loading
            val position = pagerViewModel.position
            if (it.refresh is LoadState.NotLoading && isPositionSettable(position)) {
                fragmentPager.setCurrentItem(position, false)
            }
        }
        pagerViewModel.illusts.observe(this, {
            adapter.submitData(lifecycle, it)
        })
        if (illustId > 0) {
            pagerViewModel.isSuccess.observe(this, { success ->
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

    private fun isPositionSettable(position: Int): Boolean {
        return adapter.itemCount > position && fragmentPager.currentItem != position
    }

    override fun onTokenLoaded(token: Token) {
        adapter.token = token
        val query = query ?: return
        adapter.query = query
        pagerViewModel.action = ActionDetail(token, query, pagerViewModel.position)
        if (illustId > 0) {
            fetchIllustFromNet()
        }
    }

    private fun fetchIllustFromNet() {
        pagerViewModel.fetch(illustId = illustId)
    }
}