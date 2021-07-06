package onlymash.materixiv.ui.module.novel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.Slider
import kotlinx.coroutines.flow.collectLatest
import onlymash.materixiv.R
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.isFailed
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.data.repository.novel.NovelDetailRepositoryImpl
import onlymash.materixiv.databinding.ActivityNovelReaderBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.viewbinding.viewBinding
import org.kodein.di.instance

class NovelReaderActivity : TokenActivity() {

    companion object {
        private const val NOVEL_ID_KEY = "novel_id"
        private const val NOVEL_TITLE_KEY = "novel_title"
        private const val NOVEL_AUTHOR_KEY = "novel_author"
        fun startActivity(context: Context, novelId: Long, title: String, author: String) {
            context.startActivity(Intent(context, NovelReaderActivity::class.java).apply {
                putExtra(NOVEL_ID_KEY, novelId)
                putExtra(NOVEL_TITLE_KEY, title)
                putExtra(NOVEL_AUTHOR_KEY, author)
            })
        }
    }

    private val pixivAppApi by instance<PixivAppApi>()
    private val binding by viewBinding(ActivityNovelReaderBinding::inflate)
    private lateinit var novelReaderViewModel: NovelReaderViewModel

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = intent?.extras?.getString(NOVEL_TITLE_KEY)
            subtitle = intent?.extras?.getString(NOVEL_AUTHOR_KEY)
        }
        novelReaderViewModel = getViewModel(NovelReaderViewModel(NovelDetailRepositoryImpl(pixivAppApi)))
        if (savedInstanceState == null) {
            val id = intent?.getLongExtra(NOVEL_ID_KEY, -1L)
            if (id != null && id > -1L) {
                novelReaderViewModel.novelId = id
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
                    binding.novelText.text = novel.novelText
                }
            }
        }
        binding.retryButton.setOnClickListener {
            novelReaderViewModel.refresh()
        }
        novelReaderViewModel.fontSize.observe(this, { size ->
            binding.novelText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        })
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
            R.id.action_font_size -> {
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
                    }
                }
                dialog.show()
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
}