package onlymash.materixiv.ui.module.novel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import onlymash.materixiv.data.repository.bookmarks.BookmarksRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class NovelBookmarksViewModel(
    private val repo: BookmarksRepository
) : ScopeViewModel() {

    private val _clearListCh = Channel<Unit>(Channel.CONFLATED)
    private val _auth = MutableLiveData<String>()

    val bookmarks = flowOf(
        _clearListCh.receiveAsFlow().map { PagingData.empty() },
        _auth.asFlow()
            .flatMapLatest { repo.getBookmarks(it) }
            .cachedIn(viewModelScope)
    )
        .flattenMerge(2)

    fun show(auth: String) {
        if (_auth.value == auth) {
            return
        }
        _clearListCh.trySend(Unit)
        _auth.value = auth
    }
}