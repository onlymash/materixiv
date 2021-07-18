package onlymash.materixiv.ui.module.novel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import onlymash.materixiv.app.Settings
import onlymash.materixiv.data.model.NovelTextResponse
import onlymash.materixiv.data.model.common.Novel
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.novel.NovelReaderRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class NovelReaderViewModel(private val repo: NovelReaderRepository) : ScopeViewModel() {

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

    private val _novelText = combine(_novelId, _auth, _index) { id, auth, _ ->
        fetchText(id, auth)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    val novelText: Flow<NovelTextResponse?> = _novelText

    private val _novelDetail = combine(_novelId, _auth, _index) { id, auth, _ ->
        fetchDetail(id, auth)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    val novelDetail: Flow<Novel?> = _novelDetail

    fun updateBookmark(isBookmarked: Boolean) {
        _novelDetail.value?.isBookmarked = isBookmarked
    }

    fun updateMarker(isMarked: Boolean) {
        _novelText.value?.novelMarker?.page = if (isMarked) 1 else 0
    }

    fun clearText() {
        _novelText.value?.novelText = ""
    }

    private suspend fun fetchText(id: Long, auth: String): NovelTextResponse? {
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

    private suspend fun fetchDetail(id: Long, auth: String): Novel? {
        if (auth.isBlank()) return null
        return try {
            repo.getNovelDetail(auth, id)
        } catch (_: Exception) {
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

    val authorId get() = _novelDetail.value?.user?.id ?: -1L
    val prevNovelId get() = _novelText.value?.seriesPrev?.id ?: -1L
    val nextNovelId get() = _novelText.value?.seriesNext?.id ?: -1L
}