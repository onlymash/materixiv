package onlymash.materixiv.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import onlymash.materixiv.data.model.common.UserPreview

@Serializable
data class UserResponse(
    @SerialName("next_url")
    val nextUrl: String?,
    @SerialName("user_previews")
    val userPreviews: List<UserPreview>
)