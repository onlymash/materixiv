package onlymash.materixiv.data.repository.novel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.NovelTextResponse

class NovelTextRepositoryImpl(
    private val api: PixivAppApi) : NovelTextRepository {

    override suspend fun getNovelText(
        auth: String,
        novelId: Long
    ): NovelTextResponse? {
        return withContext(Dispatchers.IO) {
            api.getNovelText(auth, novelId).body()
        }
    }
}