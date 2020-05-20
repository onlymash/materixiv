package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Novel(
    @SerialName("caption")
    val caption: String,
    @SerialName("create_date")
    val createDate: String,
    @SerialName("id")
    val id: Long = -1,
    @SerialName("image_urls")
    val imageUrls: ImageUrls,
    @SerialName("is_bookmarked")
    val isBookmarked: Boolean,
    @SerialName("is_muted")
    val isMuted: Boolean,
    @SerialName("page_count")
    val pageCount: Int,
    @SerialName("restrict")
    val restrict: Int,
    @SerialName("series")
    val series: Series?,
    @SerialName("tags")
    val tags: List<Tag>,
    @SerialName("text_length")
    val textLength: Int,
    @SerialName("title")
    val title: String,
    @SerialName("total_bookmarks")
    val totalBookmarks: Int,
    @SerialName("total_comments")
    val totalComments: Int,
    @SerialName("total_view")
    val totalView: Int,
    @SerialName("user")
    val user: User,
    @SerialName("visible")
    val visible: Boolean
)