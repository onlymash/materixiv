package onlymash.materixiv.data.repository.comment
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.Comment

class CommentDataSourceFactory(
    private val auth: String,
    private val illustId: Long,
    private val api: PixivAppApi,
    private val scope: CoroutineScope
) : DataSource.Factory<String, Comment>(){

    val sourceLiveData = MutableLiveData<CommentDataSource>()

    override fun create(): DataSource<String, Comment> {
        val source = CommentDataSource(auth, illustId, api, scope)
        sourceLiveData.postValue(source)
        return source
    }
}