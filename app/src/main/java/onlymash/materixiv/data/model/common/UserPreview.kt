package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPreview(
    @SerialName("illusts")
    val illusts: List<Illust>,
    @SerialName("is_muted")
    val isMuted: Boolean,
    @SerialName("novels")
    val novels: List<Novel>,
    @SerialName("user")
    val user: User
)