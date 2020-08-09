package onlymash.materixiv.ui.module.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.db.entity.UserCache
import onlymash.materixiv.data.repository.user.UserRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class UserViewModel(private val repo: UserRepository) : ScopeViewModel() {

    private val _action: MutableLiveData<ActionUser> = MutableLiveData()
    private val _clearListCh = Channel<Unit>(Channel.CONFLATED)

    val users = flowOf(
        _clearListCh.consumeAsFlow().map { PagingData.empty<UserCache>() },
        _action.asFlow().flatMapLatest { action ->
            repo.getUsers(action)
        }
    )
        .flattenMerge(2)
        .cachedIn(viewModelScope)
        .asLiveData()

    private fun shouldShow(action: ActionUser) = _action.value != action

    fun show(action: ActionUser) {
        if (!shouldShow(action)) return
        _clearListCh.offer(Unit)
        _action.value = action
    }
}