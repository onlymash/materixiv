package onlymash.materixiv.ui.module.home

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import onlymash.materixiv.data.db.dao.TokenDao
import onlymash.materixiv.ui.base.ScopeViewModel

class HomeViewModel(private val tokenDao: TokenDao) : ScopeViewModel() {

    val tokens = tokenDao.getAllTokensFlow().distinctUntilChanged().asLiveData()

    fun deleteAllTokens() {
        viewModelScope.launch(Dispatchers.IO) {
            tokenDao.deleteAll()
        }
    }
}