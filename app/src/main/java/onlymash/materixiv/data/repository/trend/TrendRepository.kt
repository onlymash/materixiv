package onlymash.materixiv.data.repository.trend

import onlymash.materixiv.data.model.common.TrendTag

interface TrendRepository {

    suspend fun getTrendTags(auth: String, type: Int): List<TrendTag>?

}