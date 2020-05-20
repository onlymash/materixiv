package onlymash.materixiv.data.repository.comment

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.CommentResponse
import onlymash.materixiv.data.model.common.Comment
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.extensions.NetResult
import retrofit2.HttpException

class CommentDataSource(
    private val auth: String,
    private val illustId: Long,
    private val api: PixivAppApi,
    private val scope: CoroutineScope) : PageKeyedDataSource<String, Comment>() {

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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Comment>) {

    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, Comment>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoadState.postValue(NetworkState.LOADING)
        scope.launch {
            when (val result = getComments()) {
                is NetResult.Success -> {
                    retry = null
                    callback.onResult(result.data.comments, null, result.data.nextUrl)
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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Comment>) {
        networkState.postValue(NetworkState.LOADING)
        scope.launch {
            when (val result = getComments(params.key)) {
                is NetResult.Success -> {
                    retry = null
                    callback.onResult(result.data.comments, result.data.nextUrl)
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

    private suspend fun getComments(nextUrl: String? = null): NetResult<CommentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = if (nextUrl == null) {
                    api.getIllustComments(auth, illustId)
                } else {
                    api.getIllustCommentsNext(auth, nextUrl)
                }
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