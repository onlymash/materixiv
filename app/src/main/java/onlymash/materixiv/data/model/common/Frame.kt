package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Frame(
    @SerialName("delay")
    val delay: Int,
    @SerialName("file")
    val file: String
)