package onlymash.materixiv.ui.module.novel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import onlymash.materixiv.app.Settings
import onlymash.materixiv.data.model.NovelTextResponse
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.novel.NovelTextRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class NovelReaderViewModel(private val repo: NovelTextRepository) : ScopeViewModel() {

    private val _isBookmarked = MutableStateFlow(false)

    val isBookmarked = _isBookmarked.asStateFlow()

    fun updateBookmark(isMarked: Boolean) {
        _isBookmarked.value = isMarked
    }

    private val _index = MutableStateFlow(0)

    private val _novelId = MutableStateFlow(-1L)
    var novelId: Long
        get() = _novelId.value
        set(value) {
            _novelId.value = value
        }

    private val _auth = MutableStateFlow("")

    val auth get() = _auth.value

    fun updateAuth(auth: String) {
        _auth.value = auth
    }

    val loadState = MutableStateFlow(NetworkState.LOADING)

    val novel = combine(_novelId, _auth, _index) { id, auth, index ->
        fetch(id, auth)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    private suspend fun fetch(id: Long, auth: String): NovelTextResponse? {
        if (auth.isBlank()) return null
        return try {
            loadState.value = NetworkState.LOADING
            val data = repo.getNovelText(auth, id)
            loadState.value = NetworkState.LOADED
            data
        } catch (e: Exception) {
            loadState.value = NetworkState.error(e.message)
            null
        }
    }

    fun refresh() {
        _index.value++
    }

    private val _fontSize = MutableLiveData(Settings.fontSize)

    val fontSize: LiveData<Float> = _fontSize

    val currentFontSize: Float
        get() = _fontSize.value ?: 16.0f

    fun updateFontSize(size: Float) {
        _fontSize.postValue(size)
    }
}