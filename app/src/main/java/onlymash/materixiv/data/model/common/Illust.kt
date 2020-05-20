package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Illust(
    @SerialName("caption")
    val caption: String,
    @SerialName("create_date")
    val createDate: String,
    @SerialName("height")
    val height: Int,
    @SerialName("id")
    val id: Long = -1,
    @SerialName("image_urls")
    val imageUrls: ImageUrls,
    @SerialName("is_bookmarked")
    var isBookmarked: Boolean,
    @SerialName("is_muted")
    val isMuted: Boolean,
    @SerialName("meta_pages")
    val metaPages: List<MetaPage>,
    @SerialName("meta_single_page")
    val metaSinglePage: MetaSinglePage,
    @SerialName("page_count")
    val pageCount: Int,
    @SerialName("restrict")
    val restrict: Int,
    @SerialName("sanity_level")
    val sanityLevel: Int,
    @SerialName("tags")
    val tags: List<Tag>,
    @SerialName("title")
    val title: String,
    @SerialName("tools")
    val tools: List<String>,
    @SerialName("total_bookmarks")
    val totalBookmarks: Int,
    @SerialName("total_comments")
    val totalComments: Int = 0,
    @SerialName("total_view")
    val totalView: Int,
    @SerialName("type")
    val type: String,
    @SerialName("user")
    val user: User,
    @SerialName("visible")
    val visible: Boolean,
    @SerialName("width")
    val width: Int,
    @SerialName("x_restrict")
    val xRestrict: Int
) {
    val isUgoira get() = type == "ugoira"

    companion object {
        val Illust.originUrls: ArrayList<String>
            get() {
                return if (metaPages.isEmpty()) {
                    arrayListOf(metaSinglePage.originalImageUrl.toString())
                } else {
                    val list = metaPages.map { page ->
                        page.imageUrls.original ?: page.imageUrls.large
                    }
                    ArrayList(list)
                }
            }

        val Illust.previewUrls: ArrayList<String>
            get() {
                return if (metaPages.isEmpty()) {
                    arrayListOf(imageUrls.large)
                } else {
                    val list = metaPages.map { it.imageUrls.large }
                    ArrayList(list)
                }
            }
    }
}