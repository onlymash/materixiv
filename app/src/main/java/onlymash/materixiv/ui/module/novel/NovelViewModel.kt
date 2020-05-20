package onlymash.materixiv.ui.module.novel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.repository.novel.NovelRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class NovelViewModel(private val repo: NovelRepository) : ScopeViewModel() {

    private val _action: MutableLiveData<ActionNovel?> = MutableLiveData()

    private val _result = Transformations.map(_action) { action ->
        if (action != null) {
            repo.getNovels(action, viewModelScope)
        } else {
            null
        }
    }

    val novels = Transformations.switchMap(_result) { it?.pagedList }

    val refreshState = Transformations.switchMap(_result) { it?.refreshState }

    val networkState = Transformations.switchMap(_result) { it?.networkState }

    fun show(action: ActionNovel): Boolean {
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