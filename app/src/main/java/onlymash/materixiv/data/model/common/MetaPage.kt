package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaPage(
    @SerialName("image_urls")
    val imageUrls: ImageUrls
)