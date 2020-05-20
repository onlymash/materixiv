package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZipUrls(
    @SerialName("medium")
    val medium: String
)