package onlymash.materixiv.ui.module.novel

import android.os.Bundle
import android.os.Parcel
import android.view.View
import androidx.core.view.isNotEmpty
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import onlymash.materixiv.R
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.RankingMode
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.ui.module.common.SearchBarPagerFragment
import onlymash.materixiv.ui.module.home.MainActivity
import onlymash.materixiv.ui.module.search.SearchDialog
import java.util.*

class NovelPagerFragment : SearchBarPagerFragment<NovelPagerAdapter>(),
    MainActivity.BottomNavItemReselectedListener {

    override fun getTabNames(): Array<String> {
        return resources.getStringArray(R.array.novel_type_entries)
    }

    override fun getViewPagerAdapter(): NovelPagerAdapter {
        return NovelPagerAdapter(this)
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (menuView.menu.isNotEmpty()) {
                menuView.menu.clear()
            }
            when (position) {
                1 -> activity?.menuInflater?.inflate(R.menu.searchbar_novel_following, menuView.menu)
                2 -> activity?.menuInflater?.inflate(R.menu.searchbar_novel_ranking, menuView.menu)
            }
        }
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        title.setOnClickListener {
            SearchDialog.create(Values.SEARCH_TYPE_NOVEL).show(childFragmentManager, "search_novel")
        }
        title.setText(R.string.title_novel)
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
        menuView.setOnMenuItemClickListener { menuItem ->
            handleMenuClick(menuItem.itemId)
            true
        }
        (activity as? MainActivity)?.addBottomNavItemReselectedListener(this)
    }

    private fun handleMenuClick(itemId: Int) {
        when (itemId) {
            R.id.action_restrict_public -> sharedViewModel.updateRestrict(Restrict.PUBLIC)
            R.id.action_restrict_private -> sharedViewModel.updateRestrict(Restrict.PRIVATE)
            R.id.action_ranking_mode_day -> sharedViewModel.updateRankingMode(RankingMode.DAY)
            R.id.action_ranking_mode_day_male -> sharedViewModel.updateRankingMode(RankingMode.DAY_MALE)
            R.id.action_ranking_mode_day_male_r18 -> sharedViewModel.updateRankingMode(RankingMode.DAY_MALE_R18)
            R.id.action_ranking_mode_day_female -> sharedViewModel.updateRankingMode(RankingMode.DAY_FEMALE)
            R.id.action_ranking_mode_day_female_r18 -> sharedViewModel.updateRankingMode(RankingMode.DAY_FEMALE_R18)
            R.id.action_ranking_mode_day_r18 -> sharedViewModel.updateRankingMode(RankingMode.DAY_R18)
            R.id.action_ranking_mode_week -> sharedViewModel.updateRankingMode(RankingMode.WEEK)
            R.id.action_ranking_mode_week_rookie -> sharedViewModel.updateRankingMode(RankingMode.WEEK_ROOKIE)
            R.id.action_ranking_mode_week_r18 -> sharedViewModel.updateRankingMode(RankingMode.WEEK_R18)
            R.id.action_ranking_date -> pickDate()
        }
    }

    private fun pickDate() {
        val activity = activity ?: return
        if (activity.isFinishing) {
            return
        }
        val currentTimeMillis = System.currentTimeMillis()
        val minCalendar = Calendar.getInstance(Locale.US).apply {
            timeInMillis = currentTimeMillis
            add(Calendar.YEAR, -20)
        }
        val selectedTime = sharedViewModel.selectedTime
        val minTimeMillis = minCalendar.timeInMillis
        val validator = object : CalendarConstraints.DateValidator {
            override fun describeContents(): Int = 0
            override fun writeToParcel(dest: Parcel?, flags: Int) {
                dest?.writeLong(selectedTime)
            }
            override fun isValid(date: Long): Boolean {
                return date in minTimeMillis..currentTimeMillis
            }
        }
        val calendarConstraints = CalendarConstraints.Builder()
            .setStart(minTimeMillis)
            .setEnd(currentTimeMillis)
            .setOpenAt(selectedTime)
            .setValidator(validator)
            .build()
        val dialog = MaterialDatePicker.Builder.datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setCalendarConstraints(calendarConstraints)
            .setSelection(selectedTime)
            .build()
        dialog.addOnPositiveButtonClickListener { time ->
            sharedViewModel.selectedTime = time
        }
        dialog.showNow(childFragmentManager, "date_picker")
    }

    override fun onDestroyView() {
        (activity as? MainActivity)?.removeBottomNavItemReselectedListener(this)
        super.onDestroyView()
    }

    override fun onReselectedItem(itemId: Int) {
        if (itemId != R.id.navigation_novel) {
            return
        }
        val fragment = childFragmentManager.findFragmentByTag("f${viewPager.currentItem}") as? NovelFragment
        fragment?.jumpToListTop()
    }
}