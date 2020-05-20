package onlymash.materixiv.data.repository.illust

import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.data.repository.PagingRequestHelper
import onlymash.materixiv.data.repository.createStatusLiveData
import onlymash.materixiv.extensions.NetResult
import retrofit2.HttpException

class IllustBoundaryCallback(
    private val action: ActionIllust,
    private val api: PixivAppApi,
    private val scope: CoroutineScope,
    private val handleResponse: (List<Illustration>) -> Unit
) : PagedList.BoundaryCallback<Illustration>() {

    var hasNextUrl = true
    var nextUrl: String?  = null

    //PagingRequestHelper
    val helper = PagingRequestHelper()
    //network state
    val networkState = helper.createStatusLiveData()

    override fun onZeroItemsLoaded() {
        scope.launch {
            helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) { callback ->
                createCallback(0, callback)
            }
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Illustration) {
        if (!hasNextUrl) {
            return
        }
        scope.launch {
            helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) { callback ->
                createCallback(itemAtEnd.index + 1, callback)
            }
        }
    }

    private suspend fun insertItemsIntoDb(illusts: List<Illustration>, callback: PagingRequestHelper.Callback) {
        withContext(Dispatchers.IO) {
            handleResponse.invoke(illusts)
            callback.recordSuccess()
        }
    }

    private fun createCallback(offset: Int, callback: PagingRequestHelper.Callback) {
        scope.launch {
            when (val result = getIllusts(offset)) {
                is NetResult.Success -> {
                    insertItemsIntoDb(result.data, callback)
                }
                is NetResult.HttpCode -> {
                    if (result.code == 400) {
                        callback.recordRefreshToken()
                    } else {
                        callback.recordFailure(Throwable("code: ${result.code}"))
                    }
                }
                is NetResult.Error -> {
                    callback.recordFailure(Throwable(result.errorMsg))
                }
            }
        }
    }

    private suspend fun getIllusts(offset: Int): NetResult<List<Illustration>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = nextUrl?.toHttpUrl() ?: action.getUrl(offset)
                val response = api.getIllusts(action.token.auth, url)
                val data = response.body()
                if (data != null) {
                    nextUrl = data.nextUrl
                    hasNextUrl = nextUrl != null
                    val dbQuery = action.dbQuery
                    val illusts = data.illusts.mapIndexed { index, illust ->
                        Illustration(
                            query = dbQuery,
                            tokenUid = action.token.uid,
                            id = illust.id,
                            index = index + offset,
                            illust = illust
                        )
                    }
                    NetResult.Success(illusts)
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