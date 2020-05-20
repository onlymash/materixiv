package onlymash.materixiv.data.repository.user

import kotlinx.coroutines.CoroutineScope
import onlymash.materixiv.data.action.ActionUser
import onlymash.materixiv.data.model.common.UserPreview
import onlymash.materixiv.data.repository.Listing

interface UserRepository {
    fun getUsers(action: ActionUser, scope: CoroutineScope): Listing<UserPreview>
}