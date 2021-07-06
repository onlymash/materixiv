package onlymash.materixiv.data.repository.novel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.NovelDetailResponse

class NovelDetailRepositoryImpl(
    private val api: PixivAppApi) : NovelDetailRepository {

    override suspend fun getNovelDetail(
        auth: String,
        novelId: Long
    ): NovelDetailResponse? {
        return withContext(Dispatchers.IO) {
            api.getNovelDetail(auth, novelId).body()
        }
    }
}