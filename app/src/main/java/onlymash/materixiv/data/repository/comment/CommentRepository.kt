package onlymash.materixiv.data.repository.comment

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.action.ActionComment
import onlymash.materixiv.data.model.common.Comment

interface CommentRepository {

    suspend fun getComments(action: ActionComment): Flow<PagingData<Comment>>
}