package onlymash.materixiv.ui.module.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.token.TokenRepository
import onlymash.materixiv.extensions.NetResult
import onlymash.materixiv.ui.base.ScopeViewModel

class TokenViewModel(private val repo: TokenRepository) : ScopeViewModel() {

    private val _tokens = MediatorLiveData<List<Token>?>()

    val loginState = MutableLiveData<NetworkState?>()

    val refreshState = MutableLiveData<NetworkState?>()

    fun load(): LiveData<List<Token>?> {
        viewModelScope.launch {
            _tokens.addSource(repo.load()) {
                _tokens.postValue(it)
            }
        }
        return _tokens
    }

    fun fetchToken(code: String, codeVerifier: String) {
        loginState.postValue(NetworkState.LOADING)
        viewModelScope.launch {
            when (val result = repo.getToken(code, codeVerifier)) {
                is NetResult.Success -> {
                    loginState.postValue(NetworkState.LOADED)
                }
                is NetResult.HttpCode -> {
                    loginState.postValue(NetworkState.error("code: ${result.code}"))
                }
                is NetResult.Error -> {
                    loginState.postValue(NetworkState.error(result.errorMsg))
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
                is NetResult.HttpCode -> {
                    refreshState.postValue(NetworkState.error("code: ${result.code}"))
                }
                is NetResult.Error -> {
                    refreshState.postValue(NetworkState.error(result.errorMsg))
                }
            }
        }
    }
}