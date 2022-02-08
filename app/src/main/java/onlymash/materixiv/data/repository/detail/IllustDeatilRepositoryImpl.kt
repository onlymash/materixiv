package onlymash.materixiv.data.repository.detail

import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.action.ActionDetail
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.dao.IllustCacheDao
import onlymash.materixiv.data.db.entity.IllustCache
import onlymash.materixiv.data.model.common.Illust

class IllustDeatilRepositoryImpl(
    private val api: PixivAppApi,
    private val illustCacheDao: IllustCacheDao) :
    IllustDeatilRepository {

    private suspend fun fetchIllustFromNet(
        auth: String,
        id: Long
    ): Illust? {
        return try {
            api.getIllustDetail(auth = auth, illustId = id).illust
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun fetchIllustById(
        tokenUid: Long,
        illustId: Long,
        auth: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val illust = fetchIllustFromNet(auth, illustId)
            if (illust != null) {
                val illustration = IllustCache(
                    tokenUid = tokenUid,
                    id = illustId,
                    query = "id:$illustId",
                    illust = illust
                )
                illustCacheDao.insert(illustration)
            }
            illust != null
        }
    }

    override suspend fun getIllustsFromDb(action: ActionDetail): Flow<PagingData<IllustCache>> {
        return Pager(
            config = PagingConfig(pageSize = 15),
            initialKey = if (action.initialPosition > 0) action.initialPosition - 1 else 0
        ) {
            illustCacheDao.getIllusts(action.token.uid, action.query)
        }.flow
    }
}