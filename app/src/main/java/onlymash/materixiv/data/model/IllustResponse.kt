package onlymash.materixiv.data.model
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName
import onlymash.materixiv.data.model.common.Illust


@Serializable
data class IllustResponse(
    @SerialName("illusts")
    val illusts: List<Illust>,
    @SerialName("next_url")
    val nextUrl: String? = null
)