package onlymash.materixiv.ui.module.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.db.entity.IllustCache
import onlymash.materixiv.data.model.common.UgoiraMetadata
import onlymash.materixiv.data.repository.common.CommonRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class CommonViewModel(private val repo: CommonRepository) : ScopeViewModel() {

    val ugoira = MutableLiveData<UgoiraMetadata?>()

    suspend fun addFollowUser(
        auth: String,
        userId: Long,
        restrict: Restrict
    ): Boolean {
        return repo.addFollowUser(auth, userId, restrict)
    }

    suspend fun deleteFollowUser(
        auth: String,
        userId: Long
    ): Boolean {
        return repo.deleteFollowUser(auth, userId)
    }

    fun addBookmarkIllust(illust: IllustCache, auth: String, restrict: Restrict) {
        viewModelScope.launch {
            repo.addBookmarkIllust(illust, auth, restrict)
        }
    }

    fun deleteBookmarkIllust(illust: IllustCache, auth: String) {
        viewModelScope.launch {
            repo.deleteBookmarkIllust(illust, auth)
        }
    }

    fun fetchUgoiraMetadata(auth: String, illustId: Long) {
        viewModelScope.launch {
            ugoira.postValue(repo.getUgoiraMetadata(auth, illustId))
        }
    }

    private val _isMarkerNovel = MutableStateFlow(false)
    val isMarkerNovel = _isMarkerNovel.asStateFlow()
    private val _isBookmarkNovel = MutableStateFlow(false)
    val isBookmarkNovel = _isBookmarkNovel.asStateFlow()

    fun updateMarkerNovel(isMarker: Boolean) {
        _isMarkerNovel.value = isMarker
    }

    fun updateBookmarkNovel(isMarker: Boolean) {
        _isBookmarkNovel.value = isMarker
    }

    fun addMarkerNovel(auth: String, novelId: Long) {
        viewModelScope.launch {
            if (repo.addMarkerNovel(auth, novelId)) {
                _isMarkerNovel.value = true
            }
        }
    }

    fun deleteMarkerNovel(auth: String, novelId: Long) {
        viewModelScope.launch {
            if (repo.deleteMarkerNovel(auth, novelId)) {
                _isMarkerNovel.value = false
            }
        }
    }

    fun addBookmarkNovel(auth: String, novelId: Long, restrict: Restrict) {
        viewModelScope.launch {
            if (repo.addBookmarkNovel(auth, novelId, restrict)) {
                _isBookmarkNovel.value = true
            }
        }
    }

    fun deleteBookmarkNovel(auth: String, novelId: Long) {
        viewModelScope.launch {
            if (repo.deleteBookmarkNovel(auth, novelId)) {
                _isBookmarkNovel.value = false
            }
        }
    }
}