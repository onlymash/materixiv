package onlymash.materixiv.data.repository.detail

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.action.ActionDetail
import onlymash.materixiv.data.db.entity.IllustCache

interface IllustDeatilRepository {

    suspend fun fetchIllustById(
        tokenUid: Long,
        illustId: Long,
        auth: String
    ): Boolean

    suspend fun getIllustsFromDb(action: ActionDetail): Flow<PagingData<IllustCache>>
}