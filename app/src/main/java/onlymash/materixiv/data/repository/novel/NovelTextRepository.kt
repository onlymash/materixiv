package onlymash.materixiv.data.repository.novel

import onlymash.materixiv.data.model.NovelTextResponse

interface NovelTextRepository {
    suspend fun getNovelText(auth: String, novelId: Long): NovelTextResponse?
}