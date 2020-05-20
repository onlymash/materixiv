package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Workspace(
    @SerialName("chair")
    val chair: String,
    @SerialName("comment")
    val comment: String,
    @SerialName("desk")
    val desk: String,
    @SerialName("desktop")
    val desktop: String,
    @SerialName("monitor")
    val monitor: String,
    @SerialName("mouse")
    val mouse: String,
    @SerialName("music")
    val music: String,
    @SerialName("pc")
    val pc: String,
    @SerialName("printer")
    val printer: String,
    @SerialName("scanner")
    val scanner: String,
    @SerialName("tablet")
    val tablet: String,
    @SerialName("tool")
    val tool: String,
    @SerialName("workspace_image_url")
    val workspaceImageUrl: String?
)