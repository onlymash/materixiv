package onlymash.materixiv.data.repository.common

import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.db.entity.IllustCache
import onlymash.materixiv.data.model.common.UgoiraMetadata

interface CommonRepository {

    suspend fun addFollowUser(auth: String, userId: Long, restrict: Restrict): Boolean

    suspend fun deleteFollowUser(auth: String, userId: Long): Boolean

    suspend fun addBookmarkIllust(illust: IllustCache, auth: String, restrict: Restrict): Boolean

    suspend fun deleteBookmarkIllust(illust: IllustCache, auth: String): Boolean

    suspend fun getUgoiraMetadata(auth: String, illustId: Long): UgoiraMetadata?

    suspend fun addMarkerNovel(auth: String, novelId: Long): Boolean

    suspend fun deleteMarkerNovel(auth: String, novelId: Long): Boolean

    suspend fun addBookmarkNovel(auth: String, novelId: Long, restrict: Restrict): Boolean

    suspend fun deleteBookmarkNovel(auth: String, novelId: Long): Boolean
}