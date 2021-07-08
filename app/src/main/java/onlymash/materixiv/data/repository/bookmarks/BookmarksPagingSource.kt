package onlymash.materixiv.data.repository.bookmarks

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okhttp3.HttpUrl.Companion.toHttpUrl
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.NovelMarkerResponse

class BookmarksPagingSource(
    private val auth: String,
    private val api: PixivAppApi
) : PagingSource<String, NovelMarkerResponse.MarkedNovel>() {

    companion object {
        private const val DEFAULT_URL = "https://app-api.pixiv.net/v2/novel/markers"
    }

    override fun getRefreshKey(state: PagingState<String, NovelMarkerResponse.MarkedNovel>): String? {
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)
        }
        return DEFAULT_URL
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, NovelMarkerResponse.MarkedNovel> {
        val url = params.key ?: DEFAULT_URL
        return try {
            val response = api.getMarkedNovel(auth, url.toHttpUrl())
            LoadResult.Page(
                data = response.markedNovels,
                prevKey = null,
                nextKey = response.nextUrl
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}