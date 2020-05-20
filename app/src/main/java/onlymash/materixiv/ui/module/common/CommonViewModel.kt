package onlymash.materixiv.ui.module.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.data.model.common.UgoiraMetadata
import onlymash.materixiv.data.repository.common.CommonRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class CommonViewModel(private val repo: CommonRepository) : ScopeViewModel() {

    val followState = MutableLiveData<Boolean>()

    val ugoira = MutableLiveData<UgoiraMetadata?>()

    fun addFollowUser(auth: String, userId: Long, restrict: Restrict) {
        viewModelScope.launch {
            followState.postValue(repo.addFollowUser(auth, userId, restrict))
        }
    }

    fun deleteFollowUser(auth: String, userId: Long) {
        viewModelScope.launch {
            followState.postValue(!repo.deleteFollowUser(auth, userId))
        }
    }

    fun addBookmarkIllust(illust: Illustration, auth: String, restrict: Restrict) {
        viewModelScope.launch {
            repo.addBookmarkIllust(illust, auth, restrict)
        }
    }

    fun deleteBookmarkIllust(illust: Illustration, auth: String) {
        viewModelScope.launch {
            repo.deleteBookmarkIllust(illust, auth)
        }
    }

    fun fetchUgoiraMetadata(auth: String, illustId: Long) {
        viewModelScope.launch {
            ugoira.postValue(repo.getUgoiraMetadata(auth, illustId))
        }
    }
}