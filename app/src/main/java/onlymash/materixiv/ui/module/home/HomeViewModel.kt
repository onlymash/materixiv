package onlymash.materixiv.ui.module.home

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.db.dao.TokenDao
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.ui.base.ScopeViewModel

class HomeViewModel(private val tokenDao: TokenDao) : ScopeViewModel() {

    val tokens = MediatorLiveData<List<Token>>()

    fun loadTokens() {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                tokenDao.getAllTokensLiveData()
            }
            tokens.addSource(data) {
                tokens.postValue(it)
            }
        }
    }

    fun deleteAllTokens() {
        GlobalScope.launch(Dispatchers.IO) {
            tokenDao.deleteAll()
        }
    }
}