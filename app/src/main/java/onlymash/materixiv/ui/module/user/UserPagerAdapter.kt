package onlymash.materixiv.ui.module.user

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import onlymash.materixiv.app.Values

class UserPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserFragment.create(Values.PAGE_TYPE_RECOMMENDED)
            else -> UserFragment.create(Values.PAGE_TYPE_FOLLOWING)
        }
    }

    override fun getItemCount(): Int = 2
}