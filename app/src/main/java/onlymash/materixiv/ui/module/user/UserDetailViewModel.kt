package onlymash.materixiv.ui.module.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import onlymash.materixiv.data.model.UserDetailResponse
import onlymash.materixiv.data.repository.detail.UserDetailRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class UserDetailViewModel(private val repo: UserDetailRepository) : ScopeViewModel() {

    val userDetail = MutableLiveData<UserDetailResponse?>()

    val isFailed = MutableLiveData(false)

    fun fetchUserDetail(auth: String, userId: String) {
        viewModelScope.launch {
            val result = repo.getUserDetail(auth, userId)
            isFailed.postValue(result == null)
            userDetail.postValue(result)
        }
    }

    fun updateFollowState(isFollowed: Boolean) {
        userDetail.value?.user?.isFollowed = isFollowed
    }
}