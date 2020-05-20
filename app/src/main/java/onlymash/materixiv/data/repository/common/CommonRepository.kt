package onlymash.materixiv.data.repository.common

import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.data.model.common.UgoiraMetadata

interface CommonRepository {

    suspend fun addFollowUser(auth: String, userId: Long, restrict: Restrict): Boolean

    suspend fun deleteFollowUser(auth: String, userId: Long): Boolean

    suspend fun addBookmarkIllust(illust: Illustration, auth: String, restrict: Restrict): Boolean

    suspend fun deleteBookmarkIllust(illust: Illustration, auth: String): Boolean

    suspend fun getUgoiraMetadata(auth: String, illustId: Long): UgoiraMetadata?
}