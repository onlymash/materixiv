package onlymash.materixiv.ui.module.user

import android.os.Bundle
import android.view.View
import androidx.core.view.isNotEmpty
import androidx.viewpager2.widget.ViewPager2
import onlymash.materixiv.R
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.ui.module.common.SearchBarPagerFragment
import onlymash.materixiv.ui.module.search.SearchDialog

class UserPagerFragment : SearchBarPagerFragment<UserPagerAdapter>() {

    override fun getTabNames(): Array<String> {
        return resources.getStringArray(R.array.user_type_entries)
    }

    override fun getViewPagerAdapter(): UserPagerAdapter {
        return UserPagerAdapter(this)
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (menuView.menu.isNotEmpty()) {
                menuView.menu.clear()
            }
            if (position == 1) {
                activity?.menuInflater?.inflate(R.menu.searchbar_user_following, menuView.menu)
            }
        }
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        title.setOnClickListener {
            SearchDialog.create(Values.SEARCH_TYPE_USER).show(childFragmentManager, "search_user")
        }
        title.setText(R.string.title_user)
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
        menuView.setOnMenuItemClickListener { menuItem ->
            handleMenuClick(menuItem.itemId)
            true
        }
    }

    private fun handleMenuClick(itemId: Int) {
        when (itemId) {
            R.id.action_restrict_public -> sharedViewModel.updateRestrict(Restrict.PUBLIC)
            R.id.action_restrict_private -> sharedViewModel.updateRestrict(Restrict.PRIVATE)
        }
    }
}