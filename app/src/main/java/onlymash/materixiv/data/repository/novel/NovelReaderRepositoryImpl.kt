package onlymash.materixiv.data.repository.novel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.NovelTextResponse
import onlymash.materixiv.data.model.common.Novel

class NovelReaderRepositoryImpl(
    private val api: PixivAppApi) : NovelReaderRepository {

    override suspend fun getNovelText(auth: String, novelId: Long): NovelTextResponse {
        return withContext(Dispatchers.IO) {
            api.getNovelText(auth, novelId)
        }
    }

    override suspend fun getNovelDetail(auth: String, novelId: Long): Novel {
        return withContext(Dispatchers.IO) {
            api.getNovelDetail(auth, novelId).novel
        }
    }
}