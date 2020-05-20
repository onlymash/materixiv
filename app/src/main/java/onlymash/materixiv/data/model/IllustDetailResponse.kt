package onlymash.materixiv.data.model
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName
import onlymash.materixiv.data.model.common.Illust

@Serializable
data class IllustDetailResponse(
    @SerialName("illust")
    val illust: Illust
)