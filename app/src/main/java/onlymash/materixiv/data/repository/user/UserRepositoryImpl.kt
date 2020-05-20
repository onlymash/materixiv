package onlymash.materixiv.data.repository.user

import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData
import kotlinx.coroutines.CoroutineScope
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.UserPreview
import onlymash.materixiv.data.repository.Listing

class UserRepositoryImpl(private val api: PixivAppApi) : UserRepository {

    override fun getUsers(action: ActionUser, scope: CoroutineScope): Listing<UserPreview> {
        val sourceFactory = UserDataSourceFactory(action, api, scope)
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