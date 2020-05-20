package onlymash.materixiv.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfilePublicity(
    @SerialName("birth_day")
    val birthDay: String,
    @SerialName("birth_year")
    val birthYear: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("job")
    val job: String,
    @SerialName("pawoo")
    val pawoo: Boolean,
    @SerialName("region")
    val region: String
)