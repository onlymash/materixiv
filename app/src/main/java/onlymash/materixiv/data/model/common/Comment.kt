package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    @SerialName("comment")
    val comment: String,
    @SerialName("date")
    val date: String,
    @SerialName("id")
    val id: Int,
    @SerialName("parent_comment")
    val parentComment: ParentComment,
    @SerialName("user")
    val user: User,
    @SerialName("has_replies")
    val hasReplies: Boolean = false
) {
    @Serializable
    data class ParentComment(
        @SerialName("comment")
        val comment: String? = null,
        @SerialName("date")
        val date: String? = null,
        @SerialName("id")
        val id: Int = -1,
        @SerialName("user")
        val user: User? = null
    )
}