package onlymash.materixiv.data.model
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName
import onlymash.materixiv.data.model.common.TrendTag


@Serializable
data class TrendTagResponse(
    @SerialName("trend_tags")
    val trendTags: List<TrendTag>
)