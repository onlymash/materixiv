package onlymash.materixiv.ui.module.trend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import onlymash.materixiv.data.model.common.TrendTag
import onlymash.materixiv.data.repository.trend.TrendRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class TrendViewModel(private val repo: TrendRepository): ScopeViewModel() {

    private val _auth = MutableLiveData<String>()
    private val _type = MutableLiveData<Int>()

    val trendTags = MutableLiveData<List<TrendTag>?>()

    val loading = MutableLiveData(false)

    private fun shouldFetch(auth: String, type: Int): Boolean {
        var isFetch = false
        if (_auth.value != auth) {
            _auth.value = auth
            isFetch = true
        }
        if (_type.value != type) {
            _type.value = type
            isFetch = true
        }
        return isFetch
    }

    fun fetchTrendTags(auth: String, type: Int) {
        if (shouldFetch(auth, type)) {
            refresh(auth, type)
        }
    }

    fun refresh(auth: String, type: Int) {
        viewModelScope.launch {
            loading.postValue(true)
            trendTags.postValue(repo.getTrendTags(auth, type))
            loading.postValue(false)
        }
    }
}