package onlymash.materixiv.ui.module.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import onlymash.materixiv.data.model.common.Tag
import onlymash.materixiv.data.repository.autocomplete.AutocompleteRepository
import onlymash.materixiv.ui.base.ScopeViewModel

class AutocompleteViewModel(private val repo: AutocompleteRepository) : ScopeViewModel() {

    val tags = MutableLiveData<List<Tag>>(listOf())

    val names = MutableLiveData<List<String>>(listOf())

    fun fetchTags(auth: String, word: String?) {
        viewModelScope.launch {
            tags.postValue(repo.getTags(auth, word) ?: listOf())
        }
    }

    fun fetchUsers(auth: String, word: String?) {
        viewModelScope.launch {
            names.postValue(repo.getUsers(auth, word) ?: listOf())
        }
    }
}