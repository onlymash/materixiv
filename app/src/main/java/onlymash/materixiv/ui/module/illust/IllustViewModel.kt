package onlymash.materixiv.ui.module.illust

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.db.entity.IllustCache
import onlymash.materixiv.data.repository.illust.IllustRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class IllustViewModel(private val repo: IllustRepository) : ScopeViewModel() {

    private val _action = MutableLiveData<ActionIllust>()
    private val _clearListCh = Channel<Unit>(Channel.CONFLATED)

    val illusts = flowOf(
        _clearListCh.consumeAsFlow().map { PagingData.empty<IllustCache>() },
        _action.asFlow().flatMapLatest { repo.getIllusts(it) }
    )
        .flattenMerge(2)
        .cachedIn(viewModelScope)
        .asLiveData()

    private fun shouldShow(action: ActionIllust) = _action.value != action

    fun show(action: ActionIllust) {
        if (!shouldShow(action)) return
        _clearListCh.offer(Unit)
        _action.value = action
    }
}