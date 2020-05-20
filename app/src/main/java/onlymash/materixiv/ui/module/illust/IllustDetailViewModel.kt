package onlymash.materixiv.ui.module.illust

import androidx.lifecycle.ViewModel
import onlymash.materixiv.data.db.dao.IllustDao

class IllustDetailViewModel(
    illustDao: IllustDao,
    tokenUid: Long,
    query: String,
    illustId: Long
) : ViewModel() {

    val illust = illustDao.getIllustration(tokenUid = tokenUid, query = query, id = illustId)
}