package onlymash.materixiv.data.repository.illust

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData
import androidx.room.withTransaction
import kotlinx.coroutines.*
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.MyDatabase
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.data.repository.Listing
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.extensions.NetResult
import retrofit2.HttpException

class IllustRepositoryImpl(
    private val db: MyDatabase,
    private val api: PixivAppApi) : IllustRepository {

    private var illustBoundaryCallback: IllustBoundaryCallback? = null

    private fun insertResultToDb(illusts: List<Illustration>) {
        try {
            db.illustDao().insert(illusts)
        } catch (e: SQLiteConstraintException) { }
    }

    override fun getIllusts(action: ActionIllust, scope: CoroutineScope): Listing<Illustration> {
        illustBoundaryCallback = IllustBoundaryCallback(
            action = action,
            api = api,
            scope = scope,
            handleResponse = this::insertResultToDb
        )
        val refreshTrigger = MutableLiveData<Unit?>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refreshIllusts(action, scope)
        }
        val livePagedList = db.illustDao().getIllustrations(action.token.uid, action.dbQuery)
            .toLiveData(
                config = Config(
                    pageSize = 30,
                    enablePlaceholders = true
                ),
                fetchExecutor = Dispatchers.IO.asExecutor(),
                boundaryCallback = illustBoundaryCallback
            )
        return Listing(
            pagedList = livePagedList,
            refreshState = refreshState,
            networkState = illustBoundaryCallback!!.networkState,
            refresh = {
                refreshTrigger.value = null
            },
            retry = {
                scope.launch {
                    illustBoundaryCallback?.helper?.retryAllFailed()
                }
            }
        )
    }

    private fun refreshIllusts(action: ActionIllust, scope: CoroutineScope): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        val dbQuery = action.dbQuery
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val response = api.getIllusts(action.token.auth, action.getUrl(0))
                    val data = response.body()
                    if (data != null) {
                        illustBoundaryCallback?.apply {
                            nextUrl = data.nextUrl
                            hasNextUrl = nextUrl != null
                        }
                        val illusts = data.illusts.mapIndexed { index, illust ->
                            Illustration(
                                query = dbQuery,
                                tokenUid = action.token.uid,
                                id = illust.id,
                                index = index,
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
            when (result) {
                is NetResult.Success -> {
                    val success = db.withTransaction {
                        db.illustDao().deleteIllustrations(action.token.uid, dbQuery)
                        insertResultToDb(result.data)
                        true
                    }
                    if (success) {
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        networkState.postValue(NetworkState.LOADED)
                    }
                }
                is NetResult.HttpCode -> {
                    if (result.code == 400) {
                        networkState.postValue(NetworkState.REFRESH_TOKEN)
                    } else {
                        networkState.postValue(NetworkState.error("code: ${result.code}"))
                    }
                }
                is NetResult.Error -> networkState.postValue(NetworkState.error(result.errorMsg))
            }
        }
        return networkState
    }
}