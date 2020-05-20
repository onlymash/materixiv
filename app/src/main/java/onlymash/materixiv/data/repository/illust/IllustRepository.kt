package onlymash.materixiv.data.repository.illust

import kotlinx.coroutines.CoroutineScope
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.data.repository.Listing

interface IllustRepository {

    fun getIllusts(action: ActionIllust, scope: CoroutineScope): Listing<Illustration>
}