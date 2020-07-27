package onlymash.materixiv.ui.module.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import onlymash.materixiv.data.action.*
import onlymash.materixiv.data.db.entity.Token
import java.util.*

class SharedViewModel : ViewModel() {

    val token = MutableLiveData<Token>()

    fun updateToken(token: Token) {
        if (this.token.value != token) {
            this.token.postValue(token)
        }
    }

    val restrict = MutableLiveData(Restrict.PUBLIC)

    fun updateRestrict(restrict: Restrict) {
        if (this.restrict.value != restrict) {
            this.restrict.postValue(restrict)
        }
    }

    private val _selectedTime = MutableLiveData(getTime())

    //ranking
    val date = MutableLiveData<String?>()
    val rankingMode = MutableLiveData(RankingMode.DAY)
    val rankingModeValue get() = rankingMode.value ?: RankingMode.DAY
    //search
    val sort = MutableLiveData(Sort.DATE_DESC)
    val searchTarget = MutableLiveData(SearchTarget.PARTIAL_MATCH)
    val duration = MutableLiveData<Duration>()

    var selectedTime: Long
        get() = _selectedTime.value ?: getTime()
        set(value) {
            _selectedTime.value = value
            val time = dateString
            if (date.value != time) {
                date.postValue(time)
            }
        }

    private val dateString: String
        get() {
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = selectedTime
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val realMonth = month + 1
            val monthString = if (realMonth < 10) "0$realMonth" else realMonth.toString()
            val dayString = if (day < 10) "0$day" else day.toString()
            return "$year-$monthString-$dayString"
        }

    private fun getTime(): Long {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.add(Calendar.DATE, -1)
        return calendar.timeInMillis
    }

    fun updateDate(date: String) {
        if (this.date.value != date) {
            this.date.postValue(date)
        }
    }

    fun updateRankingMode(mode: RankingMode) {
        if (rankingMode.value != mode) {
            rankingMode.postValue(mode)
        }
    }

    fun updateSort(sort: Sort) {
        if (this.sort.value != sort) {
            this.sort.postValue(sort)
        }
    }

    fun updateSearchTarget(target: SearchTarget) {
        if (searchTarget.value != target) {
            searchTarget.postValue(target)
        }
    }

    fun updateDuration(duration: Duration) {
        if (this.duration.value != duration) {
            this.duration.postValue(duration)
        }
    }
}