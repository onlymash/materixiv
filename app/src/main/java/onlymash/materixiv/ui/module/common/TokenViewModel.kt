package onlymash.materixiv.ui.module.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.token.TokenRepository
import onlymash.materixiv.extensions.NetResult
import onlymash.materixiv.ui.base.ScopeViewModel

class TokenViewModel(private val repo: TokenRepository) : ScopeViewModel() {

    val loginState = MutableLiveData<NetworkState?>()

    val refreshState = MutableLiveData<NetworkState?>()

    val tokens: LiveData<List<Token>> = repo.getAllTokens().asLiveData()

    fun fetchToken(code: String, codeVerifier: String) {
        loginState.postValue(NetworkState.LOADING)
        viewModelScope.launch {
            when (val result = repo.getToken(code, codeVerifier)) {
                is NetResult.Success -> {
                    loginState.postValue(NetworkState.LOADED)
                }
                is NetResult.Error -> {
                    loginState.postValue(NetworkState.error(result.e.message))
                }
            }
        }
    }

    fun refresh(uid: Long, refreshToken: String) {
        refreshState.postValue(NetworkState.LOADING)
        viewModelScope.launch {
            when (val result = repo.refresh(uid, refreshToken)) {
                is NetResult.Success -> {
                    refreshState.postValue(NetworkState.LOADED)
                }
                is NetResult.Error -> {
                    refreshState.postValue(NetworkState.error(result.e.message))
                }
            }
        }
    }
}