package onlymash.materixiv.ui.module.common

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayoutMediator
import onlymash.materixiv.R
import onlymash.materixiv.databinding.FragmentSearchbarPagerBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.base.ViewModelFragment
import onlymash.materixiv.ui.module.home.MainActivity
import onlymash.materixiv.ui.module.user.UserDetailActivity

abstract class SearchBarPagerFragment<T: FragmentStateAdapter> : ViewModelFragment<FragmentSearchbarPagerBinding>() {

    private val searchBarBinding get() = binding.layoutSearchbar
    protected val title get() = searchBarBinding.title
    protected val leftButton get() = searchBarBinding.leftButton
    protected val rightIcon get() = searchBarBinding.rightIcon
    protected val menuView get() = searchBarBinding.menuView
    protected val viewPager get() = binding.viewPager

    protected lateinit var sharedViewModel: SharedViewModel

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchbarPagerBinding {
        return FragmentSearchbarPagerBinding.inflate(inflater, container, false)
    }

    override fun onCreateViewModel() {
        sharedViewModel = getViewModel()
    }

    abstract fun getViewPagerAdapter(): T

    abstract fun getTabNames(): Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        leftButton.setImageDrawable(DrawerArrowDrawable(context))
        leftButton.setOnClickListener {
            val activity = activity
            if (activity is MainActivity) {
                activity.openDrawer()
            }
        }
        rightIcon.setOnClickListener { toUserDetailPage() }
        viewPager.adapter = getViewPagerAdapter()
        val tabNames = getTabNames()
        TabLayoutMediator(binding.tabs, viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
        sharedViewModel.token.observe(viewLifecycleOwner, {
            GlideApp.with(this)
                .load(it.data.user.profileImageUrls.px50x50)
                .placeholder(ContextCompat.getDrawable(rightIcon.context, R.drawable.placeholder_avatar))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(rightIcon)
        })
        onBaseViewCreated(view, savedInstanceState)
    }

    private fun toUserDetailPage() {
        val userId = sharedViewModel.token.value?.userId
        val context = context
        if (userId == null || context == null) {
            return
        }
        UserDetailActivity.start(context, userId)
    }

    abstract fun onBaseViewCreated(view: View, savedInstanceState: Bundle?)
}