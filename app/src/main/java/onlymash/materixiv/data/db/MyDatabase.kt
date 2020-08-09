package onlymash.materixiv.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.db.dao.DownloadDao
import onlymash.materixiv.data.db.dao.IllustCacheDao
import onlymash.materixiv.data.db.dao.TokenDao
import onlymash.materixiv.data.db.dao.UserCacheDao
import onlymash.materixiv.data.db.entity.Download
import onlymash.materixiv.data.db.entity.IllustCache
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.db.entity.UserCache

@Database(
    entities = [(Token::class), (UserCache::class), (IllustCache::class), (Download::class)],
    version = 2,
    exportSchema = true
)
@TypeConverters(MyConverters::class)
abstract class MyDatabase : RoomDatabase() {

    companion object {
        private var instance: MyDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context): MyDatabase = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, MyDatabase::class.java, Values.APP_DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .setQueryExecutor(Dispatchers.IO.asExecutor())
                .setTransactionExecutor(Dispatchers.IO.asExecutor())
                .build()
    }

    abstract fun tokenDao(): TokenDao

    abstract fun illustDao(): IllustCacheDao

    abstract fun userDao(): UserCacheDao

    abstract fun downloadDao(): DownloadDao
}