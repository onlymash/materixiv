package onlymash.materixiv.data.model
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName
import onlymash.materixiv.data.model.common.UgoiraMetadata


@Serializable
data class UgoiraResponse(
    @SerialName("ugoira_metadata")
    val ugoiraMetadata: UgoiraMetadata
)