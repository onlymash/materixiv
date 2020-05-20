package onlymash.materixiv.ui.module.user

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import onlymash.materixiv.app.Values
import onlymash.materixiv.ui.module.illust.IllustFragment
import onlymash.materixiv.ui.module.novel.NovelFragment

class UserDetailPagerAdapter(private val userId: String, fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IllustFragment.create(Values.PAGE_TYPE_BOOKMARKS, userId = userId)
            1 -> IllustFragment.create(Values.PAGE_TYPE_USER, userId = userId)
            2 -> NovelFragment.create(Values.PAGE_TYPE_BOOKMARKS, userId = userId)
            3 -> NovelFragment.create(Values.PAGE_TYPE_USER, userId = userId)
            4 -> UserFragment.create(Values.PAGE_TYPE_FOLLOWING, userId = userId)
            else -> UserFragment.create(Values.PAGE_TYPE_FRIENDS, userId = userId)
        }
    }

    override fun getItemCount(): Int {
        return 6
    }
}