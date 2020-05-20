package onlymash.materixiv.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import onlymash.materixiv.data.model.common.Tag

@Serializable
data class TagResponse(
    @SerialName("tags")
    val tags: List<Tag>
)