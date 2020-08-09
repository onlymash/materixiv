package onlymash.materixiv.ui.module.illust

import androidx.lifecycle.ViewModel
import onlymash.materixiv.data.db.dao.IllustCacheDao

class IllustDetailViewModel(
        illustCacheDao: IllustCacheDao,
        tokenUid: Long,
        query: String,
        illustId: Long
) : ViewModel() {

    val illust = illustCacheDao.getIllust(tokenUid = tokenUid, query = query, id = illustId)
}