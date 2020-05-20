package onlymash.materixiv.data.repository.detail

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.dao.IllustDao
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.data.model.common.Illust

class IllustDeatilRepositoryImpl(
    private val api: PixivAppApi,
    private val illustDao: IllustDao) :
    IllustDeatilRepository {

    private suspend fun fetchIllustFromNet(auth: String, id: Long): Illust? {
        return try {
            api.getIllustDetail(auth = auth, illustId = id).body()?.illust
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun fetchIllustById(tokenUid: Long, illustId: Long, auth: String): Boolean {
        return withContext(Dispatchers.IO) {
            val illust = fetchIllustFromNet(auth, illustId)
            if (illust != null) {
                val illustration = Illustration(
                    tokenUid = tokenUid,
                    id = illustId,
                    query = "id:$illustId",
                    index = 0,
                    illust = illust
                )
                illustDao.insert(illustration)
            }
            illust != null
        }
    }

    override suspend fun getIllustsFromDb(tokenUid: Long, query: String, initialPosition: Int): LiveData<PagedList<Illustration>> {
        return illustDao.getIllustrations(tokenUid, query).toLiveData(
            pageSize = 10,
            initialLoadKey = initialPosition,
            fetchExecutor = Dispatchers.IO.asExecutor()
        )
    }
}