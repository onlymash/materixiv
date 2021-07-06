package onlymash.materixiv.data.repository.novel

import onlymash.materixiv.data.model.NovelDetailResponse

interface NovelDetailRepository {
    suspend fun getNovelDetail(auth: String, novelId: Long): NovelDetailResponse?
}