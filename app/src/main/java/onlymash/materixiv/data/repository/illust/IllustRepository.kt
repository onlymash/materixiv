package onlymash.materixiv.data.repository.illust

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.db.entity.IllustCache

interface IllustRepository {

    fun getIllusts(action: ActionIllust): Flow<PagingData<IllustCache>>
}