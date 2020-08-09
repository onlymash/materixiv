package onlymash.materixiv.data.repository.user

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.MyDatabase
import onlymash.materixiv.data.db.entity.UserCache

class UserRepositoryImpl(
    private val api: PixivAppApi,
    private val db: MyDatabase
) : UserRepository {

    override fun getUsers(action: ActionUser): Flow<PagingData<UserCache>> {
        return Pager(
            PagingConfig(20),
            remoteMediator = UserRemoteMediator(action, api, db)
        ) {
            db.userDao().getUsers(action.tokenUid, action.dbQuery)
        }.flow
    }
}