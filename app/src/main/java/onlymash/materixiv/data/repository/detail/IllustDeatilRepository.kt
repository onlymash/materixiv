package onlymash.materixiv.data.repository.detail

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import onlymash.materixiv.data.db.entity.Illustration

interface IllustDeatilRepository {

    suspend fun fetchIllustById(tokenUid: Long, illustId: Long, auth: String): Boolean

    suspend fun getIllustsFromDb(tokenUid: Long, query: String, initialPosition: Int): LiveData<PagedList<Illustration>>
}