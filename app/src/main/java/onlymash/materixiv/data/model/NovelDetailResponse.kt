package onlymash.materixiv.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelDetailResponse(
    @SerialName("novel_text")
    val novelText: String,
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
        val title: String? = null
    )
}