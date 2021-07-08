package onlymash.materixiv.ui.module.novel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import onlymash.materixiv.R
import onlymash.materixiv.app.Settings
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.model.NovelTextResponse
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.isFailed
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.data.repository.novel.NovelTextRepositoryImpl
import onlymash.materixiv.databinding.ActivityNovelReaderBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.module.user.UserDetailActivity
import onlymash.materixiv.ui.viewbinding.viewBinding
import org.kodein.di.instance

class NovelReaderActivity : TokenActivity() {

    companion object {
        private const val NOVEL_ID_KEY = "novel_id"
        private const val NOVEL_TITLE_KEY = "novel_title"
        private const val NOVEL_AUTHOR_KEY = "novel_author"
        private const val NOVEL_AUTHOR_ID_KEY = "novel_author_id"
        private const val NOVEL_BOOKMARKED_KEY = "novel_bookmarked"
        fun startActivity(
            context: Context,
            novelId: Long,
            title: String,
            author: String,
            authorId: Long,
            isBookmarked: Boolean
        ) {
            context.startActivity(Intent(context, NovelReaderActivity::class.java).apply {
                putExtra(NOVEL_ID_KEY, novelId)
                putExtra(NOVEL_TITLE_KEY, title)
                putExtra(NOVEL_AUTHOR_KEY, author)
                putExtra(NOVEL_AUTHOR_ID_KEY, authorId)
                putExtra(NOVEL_BOOKMARKED_KEY, isBookmarked)
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
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = intent?.extras?.getString(NOVEL_TITLE_KEY)
            subtitle = intent?.extras?.getString(NOVEL_AUTHOR_KEY)
        }
        commonViewModel = getViewModel(CommonViewModel(CommonRepositoryImpl(pixivAppApi)))
        novelReaderViewModel = getViewModel(NovelReaderViewModel(NovelTextRepositoryImpl(pixivAppApi)))
        if (savedInstanceState == null) {
            val id = intent?.getLongExtra(NOVEL_ID_KEY, -1L)
            if (id != null && id > -1L) {
                novelReaderViewModel.novelId = id
            }
            val isBookmarked = intent?.getBooleanExtra(NOVEL_BOOKMARKED_KEY, false)
            if (isBookmarked != null) {
                commonViewModel.updateBookmarkNovel(isBookmarked)
            }
        }
        lifecycleScope.launchWhenCreated {
            novelReaderViewModel.loadState.collectLatest { state ->
                binding.progressBar.isVisible = state.isRunning()
                binding.errorContainer.isVisible = state.isFailed()
                binding.errorMsg.text = state.msg
            }
        }
        lifecycleScope.launchWhenCreated {
            novelReaderViewModel.novel.collectLatest { novel ->
                if (novel != null) {
                    handleNovel(novel)
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
            jumpToNewNovel(novelReaderViewModel.novel.value?.seriesPrev)
        }
        binding.nextPage.setOnClickListener {
            jumpToNewNovel(novelReaderViewModel.novel.value?.seriesNext)
        }
        lifecycleScope.launchWhenCreated {
            commonViewModel.isBookmarkNovel.collectLatest { binding.bookmark.isActivated = it }
        }
        lifecycleScope.launchWhenCreated {
            commonViewModel.isMarkerNovel.collectLatest { binding.marker.isActivated = it }
        }
        binding.bookmark.setOnClickListener {
            val auth = novelReaderViewModel.auth
            val novelId = novelReaderViewModel.novelId
            Log.w("bookmark", "$novelId : $auth")
            if (binding.bookmark.isActivated) {
                commonViewModel.deleteBookmarkNovel(auth, novelId)
            } else {
                commonViewModel.addBookmarkNovel(auth, novelId, Restrict.PUBLIC)
            }
        }
        binding.bookmark.setOnLongClickListener {
            if (!binding.bookmark.isActivated) {
                val auth = novelReaderViewModel.auth
                val novelId = novelReaderViewModel.novelId
                commonViewModel.addBookmarkNovel(auth, novelId, Restrict.PRIVATE)
            }
            true
        }
        binding.marker.setOnClickListener {
            val auth = novelReaderViewModel.auth
            val novelId = novelReaderViewModel.novelId
            if (binding.marker.isActivated) {
                commonViewModel.deleteMarkerNovel(auth, novelId)
            } else {
                commonViewModel.addMarkerNovel(auth, novelId)
            }
        }
    }

    private fun handleNovel(novel: NovelTextResponse) {
        commonViewModel.updateMarkerNovel(novel.novelMarker.page > 0)
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

    private fun jumpToNewNovel(novel: NovelTextResponse.NovelPreview?) {
        if (novel != null && novel.id > 0) {
            intent?.apply {
                putExtra(NOVEL_ID_KEY, novel.id)
                putExtra(NOVEL_TITLE_KEY, novel.title)
                putExtra(NOVEL_BOOKMARKED_KEY, novel.isBookmarked)
            }
            supportActionBar?.title = novel.title
            binding.novelText.text = null
            commonViewModel.updateBookmarkNovel(novel.isBookmarked)
            commonViewModel.updateMarkerNovel(false)
            novelReaderViewModel.novelId = novel.id
        }
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
                val authorId = intent?.getLongExtra(NOVEL_AUTHOR_ID_KEY, -1L) ?: -1L
                if (authorId > 0) {
                    UserDetailActivity.start(this, authorId.toString())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTokenLoaded(token: Token) {
        novelReaderViewModel.updateAuth(token.auth)
    }

    override fun onLoginStateChange(state: NetworkState?) {

    }

    override fun onRefreshStateChange(state: NetworkState?) {

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