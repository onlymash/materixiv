package onlymash.materixiv.ui.module.illust

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.repository.illust.IllustRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class IllustViewModel(private val repo: IllustRepository) : ScopeViewModel() {

    private val _action: MutableLiveData<ActionIllust?> = MutableLiveData()

    private val _result = Transformations.map(_action) { action ->
        if (action != null) {
            repo.getIllusts(action, viewModelScope)
        } else {
            null
        }
    }

    val illusts = Transformations.switchMap(_result) { it?.pagedList }

    val refreshState = Transformations.switchMap(_result) { it?.refreshState }

    val networkState = Transformations.switchMap(_result) { it?.networkState }

    fun show(action: ActionIllust): Boolean {
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