package onlymash.materixiv.ui.module.trend

import android.os.Bundle
import android.view.View
import onlymash.materixiv.R
import onlymash.materixiv.app.Values
import onlymash.materixiv.ui.module.common.SearchBarPagerFragment
import onlymash.materixiv.ui.module.search.SearchDialog

class TrendPagerFragment : SearchBarPagerFragment<TrendPagerAdapter>() {

    override fun getTabNames(): Array<String> {
        return resources.getStringArray(R.array.trend_type_entries)
    }

    override fun getViewPagerAdapter(): TrendPagerAdapter {
        return TrendPagerAdapter(this)
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        title.setOnClickListener {
            val type = if (viewPager.currentItem == 0) Values.SEARCH_TYPE_ILLUST else Values.SEARCH_TYPE_NOVEL
            SearchDialog.create(type).show(childFragmentManager, "search_trend")
        }
        title.setText(R.string.title_trend)
    }
}