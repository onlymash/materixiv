package onlymash.materixiv.ui.module.common

import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.*
import onlymash.materixiv.data.db.entity.Token
import java.text.SimpleDateFormat
import java.util.*

class SharedViewModel : ViewModel() {

    private val _token = MutableLiveData<Token>()
    val token: LiveData<Token> = _token

    fun updateToken(token: Token) {
        if (_token.value != token) {
            _token.postValue(token)
        }
    }

    private val _restrict = MutableLiveData(Restrict.PUBLIC)
    val restrict: LiveData<Restrict> = _restrict

    fun updateRestrict(restrict: Restrict) {
        if (_restrict.value != restrict) {
            _restrict.postValue(restrict)
        }
    }

    private val _selectedTime = MutableLiveData(getTimeWithOffset(-1))

    //ranking
    private val _date = MutableLiveData<String?>()
    val date: LiveData<String?> = _date
    private val _rankingMode = MutableLiveData(RankingMode.DAY)
    val rankingMode: LiveData<RankingMode> = _rankingMode
    val rankingModeValue get() = rankingMode.value ?: RankingMode.DAY
    //search
    private val _sort = MutableLiveData(Sort.DATE_DESC)
    val sort: LiveData<Sort> = _sort
    private val _searchTarget = MutableLiveData(SearchTarget.PARTIAL_MATCH)
    val searchTarget: LiveData<SearchTarget> = _searchTarget
    private val _duration = MutableLiveData(Duration.ALL)
    val duration: LiveData<Duration> = _duration
    private val _selectedTimeRange = MutableLiveData(Pair(getTimeWithOffset(-3), getTimeWithOffset(-1)))
    var selectedTimeRange: Pair<Long, Long>
        get() = _selectedTimeRange.value ?: Pair(getTimeWithOffset(-3), getTimeWithOffset(-1))
        set(value) {
            _selectedTimeRange.value = value
            _selectedTimeRangeString.value = Pair(formatDate(value.first), formatDate(value.second))
        }

    private val _selectedTimeRangeString = MutableLiveData(Pair(formatDate(selectedTimeRange.first), formatDate(selectedTimeRange.second)))
    val selectedTimeRangeString: LiveData<Pair<String, String>> = _selectedTimeRangeString

    var selectedTime: Long
        get() = _selectedTime.value ?: getTimeWithOffset(-1)
        set(value) {
            _selectedTime.value = value
            val time = dateString
            if (_date.value != time) {
                _date.postValue(time)
            }
        }

    private val dateString: String
        get() {
            val dateFormat = SimpleDateFormat(Values.DATE_FORMAT, Locale.getDefault())
            return dateFormat.format(Date(selectedTime))
        }

    fun updateDate(date: String) {
        if (_date.value != date) {
            _date.postValue(date)
        }
    }

    fun updateRankingMode(mode: RankingMode) {
        if (_rankingMode.value != mode) {
            _rankingMode.postValue(mode)
        }
    }

    fun updateSort(sort: Sort) {
        if (_sort.value != sort) {
            _sort.postValue(sort)
        }
    }

    fun updateSearchTarget(target: SearchTarget) {
        if (_searchTarget.value != target) {
            _searchTarget.postValue(target)
        }
    }

    fun updateDuration(duration: Duration) {
        if (_duration.value != duration) {
            _duration.postValue(duration)
        }
    }

    companion object {
        fun getTimeWithOffset(day: Int): Long {
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.add(Calendar.DATE, day)
            return calendar.timeInMillis
        }
        fun formatDate(time: Long): String {
            val dateFormat = SimpleDateFormat(Values.DATE_FORMAT, Locale.getDefault())
            return dateFormat.format(Date(time))
        }
        val lastDayRange: Pair<String, String>
            get() {
                val lastDay = formatDate(getTimeWithOffset(-1))
                return Pair(lastDay, lastDay)
            }
        val lastWeekRange: Pair<String, String>
            get() {
                val lastWeekDay = formatDate(getTimeWithOffset(-8))
                val lastDay = formatDate(getTimeWithOffset(-1))
                return Pair(lastWeekDay, lastDay)
            }
        val lastMonthRange: Pair<String, String>
            get() {
                val lastMonthDay = formatDate(getTimeWithOffset(-31))
                val lastDay = formatDate(getTimeWithOffset(-1))
                return Pair(lastMonthDay, lastDay)
            }
        val lastHalfYearRange: Pair<String, String>
            get() {
                val lastHalfYearDay = formatDate(getTimeWithOffset(-181))
                val lastDay = formatDate(getTimeWithOffset(-1))
                return Pair(lastHalfYearDay, lastDay)
            }
        val lastYearRange: Pair<String, String>
            get() {
                val lastYearDay = formatDate(getTimeWithOffset(-366))
                val lastDay = formatDate(getTimeWithOffset(-1))
                return Pair(lastYearDay, lastDay)
            }
    }
}