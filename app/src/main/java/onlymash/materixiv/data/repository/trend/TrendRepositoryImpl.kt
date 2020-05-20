package onlymash.materixiv.data.repository.trend

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.TrendTag

class TrendRepositoryImpl(private val pixivAppApi: PixivAppApi) : TrendRepository {

    override suspend fun getTrendTags(auth: String, type: Int): List<TrendTag>? {
        return withContext(Dispatchers.IO) {
            try {
                if (type == Values.SEARCH_TYPE_ILLUST) {
                    pixivAppApi.getTrendTagsIllust(auth = auth).body()?.trendTags
                } else {
                    pixivAppApi.getTrendTagsNovel(auth = auth).body()?.trendTags
                }
            } catch (_: Exception) {
                null
            }
        }
    }
}