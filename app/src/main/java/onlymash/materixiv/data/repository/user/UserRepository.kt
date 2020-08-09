package onlymash.materixiv.data.repository.user

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.db.entity.UserCache

interface UserRepository {

    fun getUsers(action: ActionUser): Flow<PagingData<UserCache>>
}