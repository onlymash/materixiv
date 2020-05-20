package onlymash.materixiv.data.repository.novel

import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData
import kotlinx.coroutines.CoroutineScope
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.Novel
import onlymash.materixiv.data.repository.Listing

class NovelRepositoryImpl(private val api: PixivAppApi) : NovelRepository {

    override fun getNovels(action: ActionNovel, scope: CoroutineScope): Listing<Novel> {
        val sourceFactory = NovelDataSourceFactory(action, api, scope)
        val livePagedList = sourceFactory.toLiveData(
            config = Config(
                pageSize = 30,
                enablePlaceholders = true
            )
        )
        val refreshState = Transformations
            .switchMap(sourceFactory.sourceLiveData) { it.initialLoadState }

        val networkState = Transformations
            .switchMap(sourceFactory.sourceLiveData) { it.networkState }

        return Listing(
            pagedList = livePagedList,
            refreshState = refreshState,
            networkState = networkState,
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFailed()
            }
        )
    }
}