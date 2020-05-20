package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("account")
    val account: String,
    @SerialName("comment")
    val comment: String? = null,
    @SerialName("id")
    val id: Long = -1,
    @SerialName("is_followed")
    var isFollowed: Boolean = false,
    @SerialName("name")
    val name: String,
    @SerialName("profile_image_urls")
    val profileImageUrls: ProfileImageUrls
)