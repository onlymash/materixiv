package onlymash.materixiv.data.repository.novel

import onlymash.materixiv.data.model.NovelTextResponse
import onlymash.materixiv.data.model.common.Novel

interface NovelReaderRepository {
    suspend fun getNovelText(auth: String, novelId: Long): NovelTextResponse?
    suspend fun getNovelDetail(auth: String, novelId: Long): Novel?
}