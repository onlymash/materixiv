package onlymash.materixiv.data.model
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName
import onlymash.materixiv.data.model.common.Comment


@Serializable
data class CommentResponse(
    @SerialName("comments")
    val comments: List<Comment>,
    @SerialName("next_url")
    val nextUrl: String?,
    @SerialName("total_comments")
    val totalComments: Int
)