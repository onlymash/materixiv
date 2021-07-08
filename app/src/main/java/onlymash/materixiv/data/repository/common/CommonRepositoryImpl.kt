package onlymash.materixiv.data.repository.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.dao.IllustCacheDao
import onlymash.materixiv.data.db.entity.IllustCache
import onlymash.materixiv.data.model.common.UgoiraMetadata

class CommonRepositoryImpl(
    private val pixivAppApi: PixivAppApi,
    private val illustCacheDao: IllustCacheDao? = null) : CommonRepository {

    override suspend fun addFollowUser(auth: String, userId: Long, restrict: Restrict): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.addFollowUser(auth = auth, userId = userId, restrict = restrict.value)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override suspend fun deleteFollowUser(auth: String, userId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.deleteFollowUser(auth = auth, userId = userId)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override suspend fun addBookmarkIllust(illust: IllustCache, auth: String, restrict: Restrict): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.addBookmarkIllust(auth = auth, illustId = illust.id, restrict = restrict.value)
                illust.illust.isBookmarked = true
                illustCacheDao?.update(illust)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override suspend fun deleteBookmarkIllust(illust: IllustCache, auth: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.deleteBookmarkIllust(auth = auth, illustId = illust.id)
                illust.illust.isBookmarked = false
                illustCacheDao?.update(illust)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override suspend fun getUgoiraMetadata(auth: String, illustId: Long): UgoiraMetadata? {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.getUgoiraMetadata(auth, illustId).ugoiraMetadata
            } catch (_: Exception) {
                null
            }
        }
    }

    override suspend fun addBookmarkNovel(
        auth: String,
        novelId: Long,
        restrict: Restrict
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.addBookmarkNovel(auth = auth, novelId = novelId, restrict = restrict.value)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    override suspend fun deleteBookmarkNovel(auth: String, novelId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.deleteBookmarkNovel(auth = auth, novelId = novelId)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    override suspend fun addMarkerNovel(auth: String, novelId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.addMarkerNovel(auth = auth, novelId = novelId)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    override suspend fun deleteMarkerNovel(auth: String, novelId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.deleteMarkerNovel(auth = auth, novelId = novelId)
                true
            } catch (_: Exception) {
                false
            }
        }
    }
}