package onlymash.materixiv.data.repository.novel

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.model.common.Novel

interface NovelRepository {
    suspend fun getNovels(action: ActionNovel): Flow<PagingData<Novel>>
}