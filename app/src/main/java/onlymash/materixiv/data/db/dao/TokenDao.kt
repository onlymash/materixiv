package onlymash.materixiv.data.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.db.entity.Token


@Dao
interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(token: Token): Long
    @Update
    fun update(token: Token)
    @Query("SELECT * FROM `tokens` ORDER BY `uid` ASC")
    fun getAllTokens(): List<Token>
    @Query("SELECT * FROM `tokens` ORDER BY `uid` ASC")
    fun getAllTokensFlow(): Flow<List<Token>>
    @Query("SELECT * FROM `tokens` WHERE `uid` = :uid")
    fun getTokenByUid(uid: Long): Token?
    @Delete
    fun delete(token: Token)
    @Query("DELETE FROM `tokens` WHERE `uid` = :uid")
    fun deleteTokenByUid(uid: Long)
    @Query("SELECT 1 FROM `tokens` LIMIT 1")
    fun isNotEmpty(): Boolean
    @Query("DELETE FROM `tokens`")
    fun deleteAll()
}