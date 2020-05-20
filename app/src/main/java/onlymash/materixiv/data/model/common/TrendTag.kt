package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendTag(
    @SerialName("illust")
    val illust: Illust,
    @SerialName("tag")
    val tag: String,
    @SerialName("translated_name")
    val translatedName: String? = null
)