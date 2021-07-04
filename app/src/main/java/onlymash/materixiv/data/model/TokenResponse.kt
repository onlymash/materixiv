package onlymash.materixiv.data.model
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class TokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("response")
    val detail: Detail,
    @SerialName("scope")
    val scope: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("user")
    val user: UserProfile
) {
    @Serializable
    data class Detail(
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("expires_in")
        val expiresIn: Int,
        @SerialName("refresh_token")
        val refreshToken: String,
        @SerialName("scope")
        val scope: String,
        @SerialName("token_type")
        val tokenType: String,
        @SerialName("user")
        val user: User
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
        val profileImageUrls: ProfileImageUrls,
        @SerialName("require_policy_agreement")
        val requirePolicyAgreement: Boolean,
        @SerialName("x_restrict")
        val xRestrict: Int
    )

    @Serializable
    data class User(
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
        val profileImageUrls: ProfileImageUrls,
        @SerialName("require_policy_agreement")
        val requirePolicyAgreement: Boolean,
        @SerialName("x_restrict")
        val xRestrict: Int
    )

    @Serializable
    data class ProfileImageUrls(
        @SerialName("px_16x16")
        val px16x16: String,
        @SerialName("px_170x170")
        val px170x170: String,
        @SerialName("px_50x50")
        val px50x50: String
    )
}