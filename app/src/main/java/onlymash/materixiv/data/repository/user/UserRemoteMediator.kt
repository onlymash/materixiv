package onlymash.materixiv.data.repository.user

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import okhttp3.HttpUrl.Companion.toHttpUrl
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.MyDatabase
import onlymash.materixiv.data.db.entity.UserCache

class UserRemoteMediator(
    private val action: ActionUser,
    private val api: PixivAppApi,
    private val db: MyDatabase
) : RemoteMediator<Int, UserCache>() {

    private val userNotEmpty: Boolean
        get() = db.userDao().isNotEmpty(action.tokenUid, action.dbQuery)

    override suspend fun load(loadType: LoadType, state: PagingState<Int, UserCache>): MediatorResult {
        val tokenUid = action.tokenUid
        val dbQuery = action.dbQuery
        val userDao = db.userDao()
        val url = when (loadType) {
            LoadType.REFRESH -> action.url
            LoadType.PREPEND -> null
            LoadType.APPEND -> if (userNotEmpty) userDao.nextUrl(tokenUid, dbQuery)?.toHttpUrl() else action.url
        }
            ?: return MediatorResult.Success(endOfPaginationReached = true)
        return try {
            val response = api.getUsers(action.auth, url)
            val nextUrl = response.nextUrl
            val items = response.userPreviews.map { userPreview ->
                UserCache(
                    tokenUid = tokenUid,
                    query = dbQuery,
                    id = userPreview.user.id,
                    nextUrl = nextUrl,
                    userPreview = userPreview
                )
            }
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    userDao.deleteUsers(tokenUid, dbQuery)
                }
                userDao.insert(items)
            }
            MediatorResult.Success(endOfPaginationReached = items.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }
}