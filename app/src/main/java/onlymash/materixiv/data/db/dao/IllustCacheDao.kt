package onlymash.materixiv.data.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import onlymash.materixiv.data.db.entity.IllustCache

@Dao
interface IllustCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(illust: IllustCache)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(illusts: List <IllustCache>)
    @Update
    fun update(illustCache: IllustCache)
    @Query("SELECT * FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query ORDER BY `uid` ASC")
    fun getIllusts(tokenUid: Long, query: String): PagingSource<Int, IllustCache>
    @Query("SELECT * FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query ORDER BY `uid` ASC")
    fun getIllustsLiveData(tokenUid: Long, query: String): LiveData<List<IllustCache>>
    @Query("SELECT * FROM `illusts` WHERE `token_uid` = :tokenUid AND `id` = :id ORDER BY `uid` ASC LIMIT 1")
    fun getIllustById(tokenUid: Long, id: Long): IllustCache?
    @Query("SELECT * FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query AND `id` = :id")
    fun getIllust(tokenUid: Long, query: String, id: Long): LiveData<IllustCache?>
    @Query("DELETE FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query")
    fun deleteIllusts(tokenUid: Long, query: String)
    @Query("SELECT `next_url` FROM `illusts` WHERE `uid` in (SELECT MAX(`uid`) FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query)")
    fun nextUrl(tokenUid: Long, query: String): String?
    @Query("SELECT 1 FROM `illusts` WHERE `token_uid` = :tokenUid AND `query` = :query LIMIT 1")
    fun isNotEmpty(tokenUid: Long, query: String): Boolean
}