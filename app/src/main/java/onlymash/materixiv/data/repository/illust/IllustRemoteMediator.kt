package onlymash.materixiv.data.repository.illust

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import okhttp3.HttpUrl.Companion.toHttpUrl
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.MyDatabase
import onlymash.materixiv.data.db.entity.IllustCache


class IllustRemoteMediator(
    private val action: ActionIllust,
    private val api: PixivAppApi,
    private val db: MyDatabase
) : RemoteMediator<Int, IllustCache>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, IllustCache>
    ): MediatorResult {
        val dbQuery = action.dbQuery
        val illustDao = db.illustDao()
        val url = when (loadType) {
            LoadType.REFRESH -> action.getUrl(0)
            LoadType.PREPEND -> null
            LoadType.APPEND -> illustDao.nextUrl(action.token.uid, dbQuery)?.toHttpUrl()
        }
            ?: return MediatorResult.Success(endOfPaginationReached = true)
        return try {
            val response = api.getIllusts(action.token.auth, url)
            val nextUrl = response.nextUrl
            val items = response.illusts.map { illust ->
                IllustCache(
                    query = dbQuery,
                    tokenUid = action.token.uid,
                    id = illust.id,
                    nextUrl = nextUrl,
                    illust = illust
                )
            }
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    illustDao.deleteIllusts(action.token.uid, dbQuery)
                }
                illustDao.insert(items)
            }
            MediatorResult.Success(endOfPaginationReached = items.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return if (db.illustDao().isNotEmpty(action.token.uid, action.dbQuery)) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }
}