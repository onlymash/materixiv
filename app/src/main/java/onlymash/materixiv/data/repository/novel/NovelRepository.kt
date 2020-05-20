package onlymash.materixiv.data.repository.novel

import kotlinx.coroutines.CoroutineScope
import onlymash.materixiv.data.action.ActionNovel
import onlymash.materixiv.data.model.common.Novel
import onlymash.materixiv.data.repository.Listing

interface NovelRepository {
    fun getNovels(action: ActionNovel, scope: CoroutineScope): Listing<Novel>
}