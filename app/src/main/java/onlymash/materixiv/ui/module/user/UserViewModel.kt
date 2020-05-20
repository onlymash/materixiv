package onlymash.materixiv.ui.module.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.repository.user.UserRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class UserViewModel(private val repo: UserRepository) : ScopeViewModel() {

    private val _action: MutableLiveData<ActionUser?> = MutableLiveData()

    private val _result = Transformations.map(_action) { action ->
        if (action != null) {
            repo.getUsers(action, viewModelScope)
        } else {
            null
        }
    }

    val users = Transformations.switchMap(_result) { it?.pagedList }

    val refreshState = Transformations.switchMap(_result) { it?.refreshState }

    val networkState = Transformations.switchMap(_result) { it?.networkState }

    fun updateFollowState(userId: Long, isFollowed: Boolean) {
        users.value?.apply {
            val index = indexOfFirst { it.user.id == userId }
            if (index >= 0) {
                get(index)?.user?.isFollowed = isFollowed
            }
        }
    }

    fun show(action: ActionUser): Boolean {
        if (_action.value == action) {
            return false
        }
        _action.value = action
        return true
    }

    fun refresh() {
        _result.value?.refresh?.invoke()
    }

    fun retry() {
        _result.value?.retry?.invoke()
    }
}