package onlymash.materixiv.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import onlymash.materixiv.data.model.common.Novel
import onlymash.materixiv.data.model.common.NovelMarker


@Serializable
data class NovelMarkerResponse(
    @SerialName("marked_novels")
    val markedNovels: List<MarkedNovel>,
    @SerialName("next_url")
    val nextUrl: String? = null
) {
    @Serializable
    data class MarkedNovel(
        @SerialName("novel")
        val novel: Novel,
        @SerialName("novel_marker")
        val novelMarker: NovelMarker
    )
}