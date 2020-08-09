package onlymash.materixiv.data.repository.illust

import androidx.paging.Pager
import androidx.paging.PagingConfig
import onlymash.materixiv.data.action.ActionIllust
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.MyDatabase

class IllustRepositoryImpl(
    private val api: PixivAppApi,
    private val db: MyDatabase
) : IllustRepository {

    override fun getIllusts(action: ActionIllust) = Pager(
        config = PagingConfig(30),
        remoteMediator = IllustRemoteMediator(action, api, db)
    ) {
        db.illustDao().getIllusts(action.token.uid, action.dbQuery)
    }.flow
}