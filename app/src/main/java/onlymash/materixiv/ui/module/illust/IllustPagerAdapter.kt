package onlymash.materixiv.ui.module.illust

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import onlymash.materixiv.app.Values

class IllustPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        val type = when (position) {
            0 -> Values.PAGE_TYPE_RECOMMENDED
            1 -> Values.PAGE_TYPE_FOLLOWING
            else -> Values.PAGE_TYPE_RANKING
        }
        return IllustFragment.create(type)
    }

    override fun getItemCount(): Int = 3
}