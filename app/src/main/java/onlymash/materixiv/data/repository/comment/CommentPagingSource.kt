package onlymash.materixiv.data.repository.comment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okhttp3.HttpUrl.Companion.toHttpUrl
import onlymash.materixiv.data.action.ActionComment
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.Comment

class CommentPagingSource(
    val action: ActionComment,
    val api: PixivAppApi
) : PagingSource<String, Comment>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Comment> {
        val url = params.key?.toHttpUrl() ?: action.url
        return try {
            val response = api.getComments(action.auth, url)
            LoadResult.Page(
                data = response.comments,
                prevKey = null,
                nextKey = response.nextUrl
            )
        } catch (ex: Exception) {
            LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Comment>): String? {
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)
        }
        return null
    }
}