package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UgoiraMetadata(
    @SerialName("frames")
    val frames: List<Frame>,
    @SerialName("zip_urls")
    val zipUrls: ZipUrls
)