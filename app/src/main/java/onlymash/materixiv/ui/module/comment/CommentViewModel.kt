package onlymash.materixiv.ui.module.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import onlymash.materixiv.data.action.ActionComment
import onlymash.materixiv.data.repository.comment.CommentRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class CommentViewModel(
    private val repo: CommentRepository
) : ScopeViewModel() {
    
    private val _action = MutableLiveData<ActionComment>()
    private val _clearListCh = Channel<Unit>(Channel.CONFLATED)

    val comments = flowOf(
        _clearListCh.receiveAsFlow().map { PagingData.empty() },
        _action.asFlow()
            .flatMapLatest { repo.getComments(it) }
            .cachedIn(viewModelScope)
    )
        .flattenMerge(2)

    private fun shouldShow(action: ActionComment) = _action.value != action

    fun show(action: ActionComment) {
        if (!shouldShow(action)) return
        _clearListCh.trySend(Unit)
        _action.value = action
    }
}