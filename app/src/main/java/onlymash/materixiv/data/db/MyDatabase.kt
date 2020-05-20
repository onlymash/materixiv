package onlymash.materixiv.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import onlymash.materixiv.data.db.dao.DownloadDao
import onlymash.materixiv.data.db.dao.IllustDao
import onlymash.materixiv.data.db.dao.TokenDao
import onlymash.materixiv.data.db.entity.Download
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.data.db.entity.Token

@Database(
    entities = [(Token::class), (Illustration::class), (Download::class)],
    version = 1,
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
            Room.databaseBuilder(context, MyDatabase::class.java, "materixiv.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .setQueryExecutor(Dispatchers.IO.asExecutor())
                .setTransactionExecutor(Dispatchers.IO.asExecutor())
                .build()
    }

    abstract fun tokenDao(): TokenDao

    abstract fun illustDao(): IllustDao

    abstract fun downloadDao(): DownloadDao
}