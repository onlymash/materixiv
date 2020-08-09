package onlymash.materixiv.data.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import onlymash.materixiv.data.db.entity.UserCache

@Dao
interface UserCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserCache)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(users: List<UserCache>)
    @Update
    fun update(user: UserCache)
    @Query("SELECT * FROM `users` WHERE `token_uid` = :tokenUid AND `query` = :query ORDER BY `uid` ASC")
    fun getUsers(tokenUid: Long, query: String): PagingSource<Int, UserCache>
    @Query("SELECT * FROM `users` WHERE `token_uid` = :tokenUid AND `query` = :query ORDER BY `uid` ASC")
    fun getUsersLiveData(tokenUid: Long, query: String): LiveData<List<UserCache>>
    @Query("SELECT * FROM `users` WHERE `token_uid` = :tokenUid AND `id` = :id ORDER BY `uid` ASC LIMIT 1")
    fun getIllustById(tokenUid: Long, id: Long): UserCache?
    @Query("SELECT * FROM `users` WHERE `token_uid` = :tokenUid AND `query` = :query AND `id` = :id")
    fun getIllust(tokenUid: Long, query: String, id: Long): LiveData<UserCache?>
    @Query("DELETE FROM `users` WHERE `token_uid` = :tokenUid AND `query` = :query")
    fun deleteUsers(tokenUid: Long, query: String)
    @Query("SELECT `next_url` FROM `users` WHERE `uid` in (SELECT MAX(`uid`) FROM `users` WHERE `token_uid` = :tokenUid AND `query` = :query)")
    fun nextUrl(tokenUid: Long, query: String): String?
    @Query("SELECT 1 FROM `users` WHERE `token_uid` = :tokenUid AND `query` = :query LIMIT 1")
    fun isNotEmpty(tokenUid: Long, query: String): Boolean
}