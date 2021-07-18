package onlymash.materixiv.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import onlymash.materixiv.data.model.common.NovelMarker

@Serializable
data class NovelTextResponse(
    @SerialName("novel_marker")
    val novelMarker: NovelMarker = NovelMarker(),
    @SerialName("novel_text")
    var novelText: String,
    @SerialName("series_prev")
    val seriesPrev: NovelPreview,
    @SerialName("series_next")
    val seriesNext: NovelPreview
) {
    @Serializable
    data class NovelPreview(
        @SerialName("id")
        val id: Long = -1L,
        @SerialName("title")
        val title: String? = null,
        @SerialName("is_bookmarked")
        val isBookmarked: Boolean = false
    )
}