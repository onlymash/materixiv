package onlymash.materixiv.data.repository.bookmarks

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.model.NovelMarkerResponse

interface BookmarksRepository {
    suspend fun getBookmarks(auth: String): Flow<PagingData<NovelMarkerResponse.MarkedNovel>>
}