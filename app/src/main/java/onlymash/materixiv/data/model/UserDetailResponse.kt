package onlymash.materixiv.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import onlymash.materixiv.data.model.common.User
import onlymash.materixiv.data.model.common.Profile
import onlymash.materixiv.data.model.common.ProfilePublicity
import onlymash.materixiv.data.model.common.Workspace

@Serializable
data class UserDetailResponse(
    @SerialName("profile")
    val profile: Profile,
    @SerialName("profile_publicity")
    val profilePublicity: ProfilePublicity,
    @SerialName("user")
    val user: User,
    @SerialName("workspace")
    val workspace: Workspace
)

