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
    val parentComment: Comment? = null,
    @SerialName("user")
    val user: User
)