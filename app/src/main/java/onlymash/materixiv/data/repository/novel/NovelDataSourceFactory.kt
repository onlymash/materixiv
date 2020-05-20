package onlymash.materixiv.data.repository.novel

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.Novel

class NovelDataSourceFactory(
    private val action: ActionNovel,
    private val api: PixivAppApi,
    private val scope: CoroutineScope
) : DataSource.Factory<String, Novel>(){

    val sourceLiveData = MutableLiveData<NovelDataSource>()

    override fun create(): DataSource<String, Novel> {
        val source = NovelDataSource(action, api, scope)
        sourceLiveData.postValue(source)
        return source
    }
}