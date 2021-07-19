package onlymash.materixiv.ui.module.comment

import android.content.Context
import android.content.Intent
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
import onlymash.materixiv.app.Keys
import onlymash.materixiv.data.action.ActionComment
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.comment.CommentRepositoryImpl
import onlymash.materixiv.databinding.ActivityRetryableListBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.NetworkLoadStateAdapter
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.paging.withLoadStateFooterSafe
import onlymash.materixiv.ui.viewbinding.viewBinding
import org.kodein.di.instance
import retrofit2.HttpException

class CommentActivity : TokenActivity() {

    companion object {
        fun start(context: Context, id: Long, type: Int = ActionComment.TYPE_ILLUST) {
            context.startActivity(Intent(context, CommentActivity::class.java).apply {
                putExtra(Keys.ILLUST_ID, id)
                putExtra(Keys.PAGE_TYPE, type)
            })
        }
    }

    private val pixivAppApi by instance<PixivAppApi>()

    private val binding by viewBinding(ActivityRetryableListBinding::inflate)
    private lateinit var commentViewModel: CommentViewModel
    private lateinit var commentAdapter: CommentAdapter

    private var id = -1L
    private var type = ActionComment.TYPE_ILLUST
    private var action: ActionComment? = null

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        id = intent.getLongExtra(Keys.ILLUST_ID, -1L)
        type = intent.getIntExtra(Keys.PAGE_TYPE, ActionComment.TYPE_ILLUST)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.title_comments)
            subtitle = id.toString()
        }
        commentAdapter = CommentAdapter()
        commentViewModel = getViewModel(CommentViewModel(CommentRepositoryImpl(pixivAppApi)))
        binding.list.apply {
            layoutManager = LinearLayoutManager(this@CommentActivity, RecyclerView.VERTICAL, false)
            adapter = commentAdapter.withLoadStateFooterSafe(NetworkLoadStateAdapter(commentAdapter))
        }
        commentAdapter.addLoadStateListener { handleNetworkState(it) }
        lifecycleScope.launchWhenCreated {
            commentViewModel.comments.collectLatest {
                commentAdapter.submitData(it)
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener { commentAdapter.refresh() }
        binding.retryButton.setOnClickListener { commentAdapter.refresh() }
    }

    override fun onTokenLoaded(token: Token) {
        val action = action?.apply { auth = token.auth } ?: ActionComment(token.auth, id)
        commentViewModel.show(action)
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
            commentAdapter.itemCount == 0
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