package onlymash.materixiv.ui.module.novel

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import onlymash.materixiv.app.Values

class NovelPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NovelFragment.create(Values.PAGE_TYPE_RECOMMENDED)
            1 -> NovelFragment.create(Values.PAGE_TYPE_FOLLOWING)
            else -> NovelFragment.create(Values.PAGE_TYPE_RANKING)
        }
    }

    override fun getItemCount(): Int = 3
}