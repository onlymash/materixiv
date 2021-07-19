package onlymash.materixiv.ui.module.novel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.Slider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import onlymash.materixiv.R
import onlymash.materixiv.app.Settings
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.ActionComment
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.model.NovelTextResponse
import onlymash.materixiv.data.model.common.Novel
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.isFailed
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.data.repository.novel.NovelReaderRepositoryImpl
import onlymash.materixiv.databinding.ActivityNovelReaderBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.comment.CommentActivity
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.module.user.UserDetailActivity
import onlymash.materixiv.ui.module.user.UserDetailFragment
import onlymash.materixiv.ui.viewbinding.viewBinding
import org.kodein.di.instance
import java.lang.Exception

class NovelReaderActivity : TokenActivity() {

    companion object {
        private const val NOVEL_ID_KEY = "novel_id"
        fun startActivity(
            context: Context,
            novelId: Long
        ) {
            context.startActivity(Intent(context, NovelReaderActivity::class.java).apply {
                putExtra(NOVEL_ID_KEY, novelId)
            })
        }
    }

    private val pixivAppApi by instance<PixivAppApi>()
    private val binding by viewBinding(ActivityNovelReaderBinding::inflate)
    private lateinit var novelReaderViewModel: NovelReaderViewModel
    private lateinit var commonViewModel: CommonViewModel

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        commonViewModel = getViewModel(CommonViewModel(CommonRepositoryImpl(pixivAppApi)))
        novelReaderViewModel = getViewModel(NovelReaderViewModel(NovelReaderRepositoryImpl(pixivAppApi)))
        if (savedInstanceState == null && intent != null) {
            var id = intent.getLongExtra(NOVEL_ID_KEY, -1L)
            if (id < 0) {
                intent.data?.let { uri ->
                    id = try {
                        uri.getQueryParameter("id")?.toLong() ?: -1L
                    } catch (_: Exception) {
                        -1L
                    }
                }
            }
            novelReaderViewModel.novelId = id
        }
        lifecycleScope.launchWhenCreated {
            novelReaderViewModel.loadState.collectLatest { state ->
                binding.progressBar.isVisible = state.isRunning()
                binding.errorContainer.isVisible = state.isFailed()
                binding.errorMsg.text = state.msg
            }
        }
        lifecycleScope.launchWhenCreated {
            novelReaderViewModel.novelText.collectLatest { novel ->
                if (novel != null) {
                    handleNovel(novel)
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            novelReaderViewModel.novelDetail.collectLatest { novel ->
                if (novel != null) {
                    handleNovelDetail(novel)
                }
            }
        }
        binding.retryButton.setOnClickListener {
            novelReaderViewModel.refresh()
        }
        novelReaderViewModel.fontSize.observe(this, { size ->
            binding.novelText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        })
        binding.prevPage.setOnClickListener {
            jumpToNewNovel(novelReaderViewModel.prevNovelId)
        }
        binding.nextPage.setOnClickListener {
            jumpToNewNovel(novelReaderViewModel.nextNovelId)
        }
        binding.bookmark.setOnClickListener {
            val auth = novelReaderViewModel.auth
            val novelId = novelReaderViewModel.novelId
            lifecycleScope.launch {
                val isBookmarked = if (binding.bookmark.isActivated) {
                    !commonViewModel.deleteBookmarkNovel(auth, novelId)
                } else {
                    commonViewModel.addBookmarkNovel(auth, novelId, Restrict.PUBLIC)
                }
                novelReaderViewModel.updateBookmark(isBookmarked)
                binding.bookmark.isActivated = isBookmarked
            }
        }
        binding.bookmark.setOnLongClickListener {
            if (!binding.bookmark.isActivated) {
                val auth = novelReaderViewModel.auth
                val novelId = novelReaderViewModel.novelId
                lifecycleScope.launch {
                    val isBookmarked = commonViewModel.addBookmarkNovel(auth, novelId, Restrict.PRIVATE)
                    novelReaderViewModel.updateBookmark(isBookmarked)
                    binding.bookmark.isActivated = isBookmarked
                }
            }
            true
        }
        binding.marker.setOnClickListener {
            val auth = novelReaderViewModel.auth
            val novelId = novelReaderViewModel.novelId
            lifecycleScope.launch {
                val isMarked = if (binding.marker.isActivated) {
                    !commonViewModel.deleteMarkerNovel(auth, novelId)
                } else {
                    commonViewModel.addMarkerNovel(auth, novelId)
                }
                novelReaderViewModel.updateMarker(isMarked)
                binding.marker.isActivated = isMarked
            }
        }
    }

    private fun handleNovel(novel: NovelTextResponse) {
        binding.marker.isActivated = novel.novelMarker.page > 0
        if (novel.seriesPrev.id < 0) {
            binding.prevPage.visibility = View.INVISIBLE
        } else {
            binding.prevPage.visibility = View.VISIBLE
        }
        if (novel.seriesNext.id < 0) {
            binding.nextPage.visibility = View.INVISIBLE
        } else {
            binding.nextPage.visibility = View.VISIBLE
        }
        binding.novelText.text = novel.novelText
    }

    private fun handleNovelDetail(novel: Novel) {
        supportActionBar?.apply {
            title = novel.title
            subtitle = novel.user.name
        }
        binding.bookmark.isActivated = novel.isBookmarked
    }

    private fun jumpToNewNovel(novelId: Long) {
        if (novelId < 0) return
        intent?.putExtra(NOVEL_ID_KEY, novelId)
        binding.novelText.text = null
        binding.bookmark.isActivated = false
        binding.marker.isActivated = false
        novelReaderViewModel.clearText()
        novelReaderViewModel.updateBookmark(false)
        novelReaderViewModel.updateMarker(false)
        novelReaderViewModel.novelId = novelId
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_novel_reader, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_font_size -> adjustFontSize()
            R.id.action_author -> {
                val authorId = novelReaderViewModel.authorId
                if (authorId > 0) {
                    UserDetailActivity.start(this, authorId.toString(), UserDetailFragment.TARGET_PAGE_NOVEL)
                }
            }
            R.id.action_share -> {
                val webUrl = "${Values.BASE_URL}/novel/show.php?id=${novelReaderViewModel.novelId}"
                startActivity(Intent.createChooser(
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, webUrl)
                    },
                    getString(R.string.common_share_via)
                ))
            }
            R.id.action_comments -> {
                CommentActivity.start(this, novelReaderViewModel.novelId, ActionComment.TYPE_NOVEL)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTokenLoaded(token: Token) {
        novelReaderViewModel.updateAuth(token.auth)
    }

    private fun adjustFontSize() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme_Fixed).apply {
            setContentView(R.layout.dialog_font_size)
        }
        val slider: Slider? = dialog.findViewById(R.id.font_size_slider)
        val title: TextView? = dialog.findViewById(R.id.title)
        slider?.apply {
            value = novelReaderViewModel.currentFontSize
            title?.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
            addOnChangeListener { _, value, _ ->
                novelReaderViewModel.updateFontSize(value)
                title?.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
                Settings.fontSize = value
            }
        }
        dialog.show()
    }
}