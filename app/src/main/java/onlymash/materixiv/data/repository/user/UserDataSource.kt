package onlymash.materixiv.data.repository.user

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.UserResponse
import onlymash.materixiv.data.model.common.UserPreview
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.extensions.NetResult
import retrofit2.HttpException

class UserDataSource(
    private val action: ActionUser,
    private val api: PixivAppApi,
    private val scope: CoroutineScope): PageKeyedDataSource<String, UserPreview>() {

    private var retry:(() -> Any)? = null

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let { pre ->
            scope.launch(Dispatchers.IO) {
                pre.invoke()
            }
        }
    }

    val initialLoadState = MutableLiveData<NetworkState>()

    val networkState = MutableLiveData<NetworkState>()

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, UserPreview>) {

    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, UserPreview>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoadState.postValue(NetworkState.LOADING)
        scope.launch {
            when (val result = getUsers()) {
                is NetResult.Success -> {
                    retry = null
                    callback.onResult(result.data.userPreviews, null, result.data.nextUrl)
                    networkState.postValue(NetworkState.LOADED)
                }
                is NetResult.HttpCode -> {
                    if (result.code == 400) {
                        networkState.postValue(NetworkState.REFRESH_TOKEN)
                        initialLoadState.postValue(NetworkState.REFRESH_TOKEN)
                    } else {
                        retry = {
                            loadInitial(params, callback)
                        }
                        networkState.postValue(NetworkState.error("code: ${result.code}"))
                        initialLoadState.postValue(NetworkState.error("code: ${result.code}"))
                    }
                }
                is NetResult.Error -> {
                    retry = {
                        loadInitial(params, callback)
                    }
                    networkState.postValue(NetworkState.error(result.errorMsg))
                    initialLoadState.postValue(NetworkState.error(result.errorMsg))
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, UserPreview>) {
        networkState.postValue(NetworkState.LOADING)
        scope.launch {
            when (val result = getUsers(params.key)) {
                is NetResult.Success -> {
                    retry = null
                    callback.onResult(result.data.userPreviews, result.data.nextUrl)
                    networkState.postValue(NetworkState.LOADED)
                }
                is NetResult.HttpCode -> {
                    if (result.code == 400) {
                        networkState.postValue(NetworkState.REFRESH_TOKEN)
                    } else {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error("code: ${result.code}"))
                    }
                }
                is NetResult.Error -> {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(NetworkState.error(result.errorMsg))
                }
            }
        }
    }

    private suspend fun getUsers(nextUrl: String? = null): NetResult<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getUsers(auth = action.auth, url = nextUrl?.toHttpUrl() ?: action.url)
                val data = response.body()
                if (data != null) {
                    NetResult.Success(data)
                } else {
                    NetResult.HttpCode(response.code())
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    NetResult.HttpCode(e.code())
                } else {
                    NetResult.Error(e.message.toString())
                }
            }
        }
    }
}