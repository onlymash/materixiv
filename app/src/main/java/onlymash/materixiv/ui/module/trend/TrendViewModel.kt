package onlymash.materixiv.ui.module.trend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import onlymash.materixiv.data.model.common.TrendTag
import onlymash.materixiv.data.repository.trend.TrendRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class TrendViewModel(private val repo: TrendRepository): ScopeViewModel() {

    val trendTags = MutableLiveData<List<TrendTag>?>()

    val loading = MutableLiveData(false)

    fun fetchTrendTags(auth: String, type: Int) {
        viewModelScope.launch {
            loading.postValue(true)
            trendTags.postValue(repo.getTrendTags(auth, type))
            loading.postValue(false)
        }
    }
}