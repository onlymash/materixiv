package onlymash.materixiv.data.repository.novel

import androidx.paging.PagingSource
import okhttp3.HttpUrl.Companion.toHttpUrl
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.Novel

class NovelPagingSource(
    private val action: ActionNovel,
    private val api: PixivAppApi
) : PagingSource<String, Novel>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Novel> {
        val url = params.key?.toHttpUrl() ?: action.url
        return try {
            val response = api.getNovels(action.auth, url)
            LoadResult.Page(
                data = response.novels,
                prevKey = null,
                nextKey = response.nextUrl
            )
        } catch (ex: Exception) {
            LoadResult.Error(ex)
        }
    }
}