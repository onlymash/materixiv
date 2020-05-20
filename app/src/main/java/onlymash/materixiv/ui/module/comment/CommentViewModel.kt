package onlymash.materixiv.ui.module.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import onlymash.materixiv.data.repository.comment.CommentRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class CommentViewModel(
    private val illustId: Long,
    private val repo: CommentRepository
) : ScopeViewModel() {


    private val _auth: MutableLiveData<String?> = MutableLiveData()

    private val _result = Transformations.map(_auth) { auth ->
        if (auth != null) {
            repo.getComments(viewModelScope, auth, illustId)
        } else {
            null
        }
    }

    val comments = Transformations.switchMap(_result) { it?.pagedList }

    val refreshState = Transformations.switchMap(_result) { it?.refreshState }

    val networkState = Transformations.switchMap(_result) { it?.networkState }

    fun show(auth: String): Boolean {
        if (_auth.value == auth) {
            return false
        }
        _auth.value = auth
        return true
    }

    fun refresh() {
        _result.value?.refresh?.invoke()
    }

    fun retry() {
        _result.value?.retry?.invoke()
    }
}