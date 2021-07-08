package onlymash.materixiv.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import onlymash.materixiv.data.model.common.Novel

@Serializable
data class NovelDetailResponse(
    @SerialName("novel")
    val novel: Novel
)