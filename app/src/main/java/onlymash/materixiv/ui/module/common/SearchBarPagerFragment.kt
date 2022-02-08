package onlymash.materixiv.ui.module.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import onlymash.materixiv.R
import onlymash.materixiv.databinding.FragmentSearchbarPagerBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.base.ViewModelFragment
import onlymash.materixiv.ui.module.home.MainActivity
import onlymash.materixiv.ui.module.user.UserDetailActivity

abstract class SearchBarPagerFragment<T: FragmentStateAdapter> : ViewModelFragment<FragmentSearchbarPagerBinding>() {

    protected lateinit var title: MaterialTextView
    protected lateinit var leftButton: AppCompatImageButton
    protected lateinit var rightIcon: ShapeableImageView
    protected lateinit var menuView: ActionMenuView
    protected lateinit var viewPager: ViewPager2

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
        title = binding.layoutSearchbar.title
        leftButton = binding.layoutSearchbar.leftButton
        rightIcon = binding.layoutSearchbar.rightIcon
        menuView = binding.layoutSearchbar.menuView
        viewPager = binding.viewPager
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
        sharedViewModel.token.observe(viewLifecycleOwner) {
            GlideApp.with(this)
                .load(it.data.user.profileImageUrls.px50x50)
                .placeholder(
                    ContextCompat.getDrawable(
                        rightIcon.context,
                        R.drawable.placeholder_avatar
                    )
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(rightIcon)
        }
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