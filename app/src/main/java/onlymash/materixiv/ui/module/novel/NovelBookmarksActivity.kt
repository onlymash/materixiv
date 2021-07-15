package onlymash.materixiv.ui.module.novel

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import onlymash.materixiv.R
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.bookmarks.BookmarksRepositoryImpl
import onlymash.materixiv.databinding.ActivityNovelBookmarksBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.NetworkLoadStateAdapter
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.viewbinding.viewBinding
import org.kodein.di.instance
import retrofit2.HttpException

class NovelBookmarksActivity : TokenActivity() {

    private val binding by viewBinding(ActivityNovelBookmarksBinding::inflate)
    private val pixivAppApi by instance<PixivAppApi>()
    private lateinit var novelBookmarksAdapter: NovelBookmarksAdapter
    private lateinit var bookmarksViewModel: NovelBookmarksViewModel

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        supportActionBar?.apply {
            setTitle(R.string.title_bookmarks)
            setDisplayHomeAsUpEnabled(true)
        }
        bookmarksViewModel = getViewModel(NovelBookmarksViewModel(BookmarksRepositoryImpl(pixivAppApi)))
        novelBookmarksAdapter = NovelBookmarksAdapter()
        binding.list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = novelBookmarksAdapter.withLoadStateFooter(NetworkLoadStateAdapter(novelBookmarksAdapter))
        }
        novelBookmarksAdapter.addLoadStateListener { handleNetworkState(it) }
        lifecycleScope.launchWhenCreated {
            bookmarksViewModel.bookmarks.collectLatest {
                novelBookmarksAdapter.submitData(it)
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener { novelBookmarksAdapter.refresh() }
        binding.retryButton.setOnClickListener {
            novelBookmarksAdapter.refresh()
        }
    }

    override fun onTokenLoaded(token: Token) {
        bookmarksViewModel.show(token.auth)
    }

    private fun handleNetworkState(loadStates: CombinedLoadStates) {
        val refresh = loadStates.source.refresh
        val append = loadStates.source.append
        binding.swipeRefreshLayout.isRefreshing = refresh is LoadState.Loading
        val error = when {
            refresh is LoadState.Error -> refresh.error
            append is LoadState.Error -> append.error
            else -> null
        }
        binding.retryButton.isVisible = if (error != null) {
            handleException(error)
            novelBookmarksAdapter.itemCount == 0
        } else false
    }

    private fun handleException(error: Throwable) {
        if (error is HttpException && error.code() == 400) {
            refreshToken()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}