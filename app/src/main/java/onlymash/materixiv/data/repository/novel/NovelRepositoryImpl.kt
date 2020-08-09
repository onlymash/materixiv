package onlymash.materixiv.data.repository.novel

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.Novel

class NovelRepositoryImpl(private val api: PixivAppApi) : NovelRepository {

    override suspend fun getNovels(action: ActionNovel): Flow<PagingData<Novel>> {
        return Pager(
            config = PagingConfig(20)
        ) {
            NovelPagingSource(action, api)
        }.flow
    }
}