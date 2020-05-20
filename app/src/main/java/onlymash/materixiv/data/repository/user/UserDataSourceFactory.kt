package onlymash.materixiv.data.repository.user

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.UserPreview

class UserDataSourceFactory(
    private val action: ActionUser,
    private val api: PixivAppApi,
    private val scope: CoroutineScope
) : DataSource.Factory<String, UserPreview>(){

    val sourceLiveData = MutableLiveData<UserDataSource>()

    override fun create(): DataSource<String, UserPreview> {
        val source = UserDataSource(action, api, scope)
        sourceLiveData.postValue(source)
        return source
    }
}