package onlymash.materixiv.data.repository.comment

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.action.ActionComment
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.Comment

class CommentRepositoryImpl(
    private val api: PixivAppApi
) : CommentRepository {

    override suspend fun getComments(action: ActionComment): Flow<PagingData<Comment>> {
        return Pager(config = PagingConfig(pageSize = 10)) {
            CommentPagingSource(action, api)
        }
            .flow
    }
}