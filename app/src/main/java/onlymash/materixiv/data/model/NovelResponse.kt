package onlymash.materixiv.data.model
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName
import onlymash.materixiv.data.model.common.Novel


@Serializable
data class NovelResponse(
    @SerialName("novels")
    val novels: List<Novel>,
    @SerialName("next_url")
    val nextUrl: String? = null
)