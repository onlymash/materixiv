package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelMarker(
    @SerialName("page")
    val page: Int = -1
)