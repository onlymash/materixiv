package onlymash.materixiv.data.repository.comment

import kotlinx.coroutines.CoroutineScope
import onlymash.materixiv.data.model.common.Comment
import onlymash.materixiv.data.repository.Listing

interface CommentRepository {
    fun getComments(scope: CoroutineScope, auth: String, illustId: Long): Listing<Comment>
}