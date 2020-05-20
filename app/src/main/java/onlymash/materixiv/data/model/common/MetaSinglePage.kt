package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaSinglePage(
    @SerialName("original_image_url")
    val originalImageUrl: String? = null
)