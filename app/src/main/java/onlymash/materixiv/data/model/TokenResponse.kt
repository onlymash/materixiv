package onlymash.materixiv.data.model
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class TokenResponse(
    @SerialName("response")
    val data: TokenInfo
) {
    @Serializable
    data class TokenInfo(
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("device_token")
        val deviceToken: String,
        @SerialName("expires_in")
        val expiresIn: Long,
        @SerialName("refresh_token")
        val refreshToken: String,
        @SerialName("scope")
        val scope: String,
        @SerialName("token_type")
        val tokenType: String,
        @SerialName("user")
        val profile: UserProfile
    )

    @Serializable
    data class UserProfile(
        @SerialName("account")
        val account: String,
        @SerialName("id")
        val id: String,
        @SerialName("is_mail_authorized")
        val isMailAuthorized: Boolean,
        @SerialName("is_premium")
        val isPremium: Boolean,
        @SerialName("mail_address")
        val mailAddress: String,
        @SerialName("name")
        val name: String,
        @SerialName("profile_image_urls")
        val profileImageUrls: UserProfileImageUrls,
        @SerialName("require_policy_agreement")
        val requirePolicyAgreement: Boolean,
        @SerialName("x_restrict")
        val xRestrict: Int
    )

    @Serializable
    data class UserProfileImageUrls(
        @SerialName("px_16x16")
        val px16x16: String,
        @SerialName("px_170x170")
        val px170x170: String,
        @SerialName("px_50x50")
        val px50x50: String
    )
}