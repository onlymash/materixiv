package onlymash.materixiv.ui.module.illust

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import kotlinx.coroutines.launch
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.detail.IllustDeatilRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class IllustDetailPagerViewModel(
    private val repo: IllustDeatilRepository
) : ScopeViewModel() {

    private val _position = MutableLiveData(0)
    var position: Int
        get() = _position.value ?: 0
        set(value) {
            _position.value = value
        }

    private val _token = MutableLiveData<Token>()
    var token: Token?
        get() = _token.value
        set(value) {
            _token.value = value
        }

    val illusts = MediatorLiveData<PagedList<Illustration>>()
    val isSuccess = MutableLiveData<Boolean>()

    fun load(tokenUid: Long, query: String, initialPosition: Int) {
        viewModelScope.launch {
            illusts.addSource(repo.getIllustsFromDb(tokenUid, query, initialPosition)) {
                illusts.postValue(it)
            }
        }
    }

    fun fetch(illustId: Long) {
        val token = token ?: return
        viewModelScope.launch {
            isSuccess.postValue(repo.fetchIllustById(tokenUid = token.uid, illustId = illustId, auth = token.auth))
        }
    }
}