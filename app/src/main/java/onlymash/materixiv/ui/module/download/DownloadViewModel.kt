package onlymash.materixiv.ui.module.download

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.db.dao.DownloadDao
import onlymash.materixiv.data.db.entity.Download
import onlymash.materixiv.ui.base.ScopeViewModel

class DownloadViewModel(private val downloadDao: DownloadDao) : ScopeViewModel() {

    val downloads = MediatorLiveData<List<Download>?>()

    fun loadAll() {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                downloadDao.getAllLiveData()
            }
            downloads.addSource(data) {
                downloads.postValue(it)
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            downloadDao.deleteAll()
        }
    }

    fun delete(uid: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadDao.delete(uid)
        }
    }

    fun deleteCompleted(downloads: List<Download>) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadDao.delete(downloads)
        }
    }
}