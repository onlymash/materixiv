package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Series(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("title")
    val title: String? = null
)