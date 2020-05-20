package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("address_id")
    val addressId: Int,
    @SerialName("background_image_url")
    val backgroundImageUrl: String? = null,
    @SerialName("birth")
    val birth: String,
    @SerialName("birth_day")
    val birthDay: String,
    @SerialName("birth_year")
    val birthYear: Int,
    @SerialName("country_code")
    val countryCode: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("is_premium")
    val isPremium: Boolean,
    @SerialName("is_using_custom_profile_image")
    val isUsingCustomProfileImage: Boolean,
    @SerialName("job")
    val job: String,
    @SerialName("job_id")
    val jobId: Int,
    @SerialName("pawoo_url")
    val pawooUrl: String? = null,
    @SerialName("region")
    val region: String,
    @SerialName("total_follow_users")
    val totalFollowUsers: Int,
    @SerialName("total_illust_bookmarks_public")
    val totalIllustBookmarksPublic: Int,
    @SerialName("total_illust_series")
    val totalIllustSeries: Int,
    @SerialName("total_illusts")
    val totalIllusts: Int,
    @SerialName("total_manga")
    val totalManga: Int,
    @SerialName("total_mypixiv_users")
    val totalMypixivUsers: Int,
    @SerialName("total_novel_series")
    val totalNovelSeries: Int,
    @SerialName("total_novels")
    val totalNovels: Int,
    @SerialName("twitter_account")
    val twitterAccount: String,
    @SerialName("twitter_url")
    val twitterUrl: String? = null,
    @SerialName("webpage")
    val webpage: String? = null
)