package onlymash.materixiv.ui.module.trend

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import onlymash.materixiv.app.Values

class TrendPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        val type = when (position) {
            0 -> Values.SEARCH_TYPE_ILLUST
            else -> Values.SEARCH_TYPE_NOVEL
        }
        return TrendFragment.create(type)
    }

    override fun getItemCount(): Int = 2
}