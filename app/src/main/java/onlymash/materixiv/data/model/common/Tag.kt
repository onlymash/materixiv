package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    @SerialName("name")
    val name: String,
    @SerialName("translated_name")
    val translatedName: String? = null,
    @SerialName("added_by_uploaded_user")
    val addedByUploadedUser: Boolean? = null
)