package onlymash.materixiv.data.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import onlymash.materixiv.data.db.entity.Download

@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(downloads: List<Download>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(download: Download): Long

    @Update
    fun update(download: Download)

    @Query("SELECT * FROM `downloads` WHERE `url` = :url")
    fun getDownload(url: String): Download?

    @Query("SELECT * FROM `downloads` WHERE `uid` = :uid")
    fun getDownload(uid: Long): Download?

    @Query("SELECT * FROM `downloads` ORDER BY `uid` DESC")
    fun getAll(): List<Download>

    @Query("SELECT * FROM `downloads` ORDER BY `uid` DESC")
    fun getAllLiveData(): LiveData<List<Download>>

    @Query("SELECT * FROM `downloads` ORDER BY `uid` DESC")
    fun getAllPagedList(): DataSource.Factory<Int, Download>

    @Query("DELETE FROM `downloads`")
    fun deleteAll()

    @Query("DELETE FROM `downloads` WHERE `uid` = :uid")
    fun delete(uid: Long)
}