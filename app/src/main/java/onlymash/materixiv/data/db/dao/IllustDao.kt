package onlymash.materixiv.data.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import onlymash.materixiv.data.db.entity.Illustration

@Dao
interface IllustDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(illust: Illustration)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(illusts: List<Illustration>)
    @Update
    fun update(illustration: Illustration)
    @Query("SELECT * FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query ORDER BY `uid` ASC")
    fun getIllustrations(tokenUid: Long, query: String): DataSource.Factory<Int, Illustration>
    @Query("SELECT * FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query ORDER BY `uid` ASC")
    fun getIllustrationsLiveData(tokenUid: Long, query: String): LiveData<List<Illustration>>
    @Query("SELECT * FROM `illusts` WHERE `token_uid` = :tokenUid AND `id` = :id ORDER BY `uid` ASC LIMIT 1")
    fun getIllustrationById(tokenUid: Long, id: Long): Illustration?
    @Query("SELECT * FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query AND `id` = :id")
    fun getIllustration(tokenUid: Long, query: String, id: Long): LiveData<Illustration?>
    @Query("DELETE FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query")
    fun deleteIllustrations(tokenUid: Long, query: String)
}