package onlymash.materixiv.ui.module.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.databinding.ActivitySearchBinding
import onlymash.materixiv.ui.module.common.TokenActivity
import onlymash.materixiv.ui.viewbinding.viewBinding

class SearchActivity : TokenActivity() {

    companion object {
        fun startSearch(
            context: Context,
            type: Int,
            word: String? = null,
            illustId: Long = -1
        ) {
            context.startActivity(Intent(context, SearchActivity::class.java).apply {
                putExtra(Keys.SEARCH_TYPE, type)
                putExtra(Keys.SEARCH_WORD, word)
                putExtra(Keys.ILLUST_ID, illustId)
            })
        }
    }

    private val binding by viewBinding(ActivitySearchBinding::inflate)

    override fun onLoadTokenBefore(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        var type = Values.SEARCH_TYPE_ILLUST
        var word = ""
        var illustId: Long = -1
        intent?.apply {
            type = getIntExtra(Keys.SEARCH_TYPE, Values.SEARCH_TYPE_ILLUST)
            word = getStringExtra(Keys.SEARCH_WORD) ?: ""
            illustId = getLongExtra(Keys.ILLUST_ID, -1)
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.search_fragment_container, SearchFragment.create(type = type, word = word, illustId = illustId))
                .commitNow()
        }
    }
}