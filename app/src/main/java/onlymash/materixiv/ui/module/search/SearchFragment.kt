package onlymash.materixiv.ui.module.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.Duration
import onlymash.materixiv.data.action.SearchTarget
import onlymash.materixiv.data.action.Sort
import onlymash.materixiv.databinding.FragmentSearchBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.base.ViewModelFragment
import onlymash.materixiv.ui.module.common.SharedViewModel
import onlymash.materixiv.ui.module.illust.IllustFragment
import onlymash.materixiv.ui.module.novel.NovelFragment
import onlymash.materixiv.ui.module.user.UserFragment

class SearchFragment : ViewModelFragment<FragmentSearchBinding>() {

    companion object {
        fun create(type: Int, word: String, illustId: Long): SearchFragment {
            return SearchFragment().apply {
                arguments = Bundle().apply {
                    putInt(Keys.SEARCH_TYPE, type)
                    putString(Keys.SEARCH_WORD, word)
                    putLong(Keys.ILLUST_ID, illustId)
                }
            }
        }
    }

    private val searchBarBinding get() = binding.layoutSearchbar
    private val leftButton get() = searchBarBinding.leftButton
    private val rightIcon get() = searchBarBinding.rightIcon
    private val menuView get() = searchBarBinding.menuView
    private val child get() = binding.childFragmentContainer
    private val title get() = searchBarBinding.title

    private var type = Values.SEARCH_TYPE_ILLUST
    private var word = ""
    private var illustId: Long = -1

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            type = getInt(Keys.SEARCH_TYPE, Values.SEARCH_TYPE_ILLUST)
            word = getString(Keys.SEARCH_WORD, "")
            illustId = getLong(Keys.ILLUST_ID, -1)
        }
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onCreateViewModel() {
        sharedViewModel = getViewModel()
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        child.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                child.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val appBarHeight = binding.appBar.height
                child.translationY = (-appBarHeight).toFloat()
                child.layoutParams.height = child.height + appBarHeight
            }
        })
        title.text = word
        leftButton.setImageDrawable(DrawerArrowDrawable(context).apply { progress = 1.0f })
        leftButton.setOnClickListener { activity?.onBackPressed() }
        title.setOnClickListener { SearchDialog.create(type).show(childFragmentManager, "search") }
        sharedViewModel.token.observe(viewLifecycleOwner, {
            GlideApp.with(rightIcon)
                .load(it.data.user.profileImageUrls.px170x170)
                .placeholder(ContextCompat.getDrawable(rightIcon.context, R.drawable.placeholder_avatar))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(rightIcon)
        })
        menuView.setOnMenuItemClickListener { menuItem ->
            handleMenuClick(menuItem.itemId)
            true
        }
        rightIcon.setOnClickListener {

        }
        if (type == Values.SEARCH_TYPE_ILLUST && illustId < 0) {
            activity?.menuInflater?.inflate(R.menu.searchbar_illust_search, menuView.menu)
            val menu = menuView.menu
            if (menu is MenuBuilder) {
                menu.setOptionalIconsVisible(true)
            }
        }
        if (savedInstanceState == null) {
            val fragment = when(type) {
                Values.SEARCH_TYPE_ILLUST -> {
                    if (illustId > 0) {
                        IllustFragment.create(type = Values.PAGE_TYPE_RELATED, illustId = illustId)
                    } else {
                        IllustFragment.create(type = Values.PAGE_TYPE_SEARCH, word = word)
                    }
                }
                Values.SEARCH_TYPE_NOVEL -> NovelFragment.create(type = Values.PAGE_TYPE_SEARCH, word = word)
                else -> UserFragment.create(type = Values.PAGE_TYPE_SEARCH, word = word)
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.child_fragment_container, fragment)
                .commitNow()
        }
    }

    private fun handleMenuClick(itemId: Int) {
        when (itemId) {
            R.id.action_sort_date_desc -> sharedViewModel.updateSort(Sort.DATE_DESC)
            R.id.action_sort_date_asc -> sharedViewModel.updateSort(Sort.DATE_ASC)
            R.id.action_sort_popular_desc -> sharedViewModel.updateSort(Sort.POPULAR_DESC)
            R.id.action_search_target_partial_match_for_tags -> sharedViewModel.updateSearchTarget(SearchTarget.PARTIAL_MATCH)
            R.id.action_search_target_exact_match_for_tags -> sharedViewModel.updateSearchTarget(SearchTarget.EXACT_MATCH)
            R.id.action_search_target_title_and_caption -> sharedViewModel.updateSearchTarget(SearchTarget.TITLE_CAPTION)
            R.id.action_duration_within_last_day -> sharedViewModel.updateDuration(Duration.LAST_DAY)
            R.id.action_duration_within_last_week -> sharedViewModel.updateDuration(Duration.LAST_WEEK)
            R.id.action_duration_within_last_month -> sharedViewModel.updateDuration(Duration.LAST_MONTH)
            R.id.action_duration_within_last_half_year -> sharedViewModel.updateDuration(Duration.HALF_YEAR)
            R.id.action_duration_within_last_year -> sharedViewModel.updateDuration(Duration.YEAR)
            R.id.action_duration_within_all -> sharedViewModel.updateDuration(Duration.ALL)
        }
    }
}