package onlymash.materixiv.data.repository.bookmarks

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.NovelMarkerResponse

class BookmarksRepositoryImpl(
    private val api: PixivAppApi) : BookmarksRepository {

    override suspend fun getBookmarks(auth: String): Flow<PagingData<NovelMarkerResponse.MarkedNovel>> {
        return Pager(PagingConfig(20)) {
            BookmarksPagingSource(auth, api)
        }
            .flow
    }
}