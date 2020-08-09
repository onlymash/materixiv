package onlymash.materixiv.ui.module.illust

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import onlymash.materixiv.data.action.ActionDetail
import onlymash.materixiv.data.db.entity.IllustCache
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

    private val _clearListCh = Channel<Unit>(Channel.CONFLATED)
    private val _action = MutableLiveData<ActionDetail>()
    var action: ActionDetail?
        get() = _action.value
        set(value) {
            value?.let {
                _action.value = it
            }
        }
    private val token get() = action?.token

    val isSuccess = MutableLiveData<Boolean>()

    val illusts = flowOf(
        _clearListCh.consumeAsFlow().map { PagingData.empty<IllustCache>() },
        _action.asFlow().flatMapLatest { repo.getIllustsFromDb(it) }
    )
        .flattenMerge(2)
        .cachedIn(viewModelScope)
        .asLiveData()

    fun fetch(illustId: Long) {
        val token = token ?: return
        viewModelScope.launch {
            isSuccess.postValue(repo.fetchIllustById(tokenUid = token.uid, illustId = illustId, auth = token.auth))
        }
    }
}