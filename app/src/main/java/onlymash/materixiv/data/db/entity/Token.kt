package onlymash.materixiv.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import onlymash.materixiv.data.model.TokenResponse

@Entity(
    tableName = "tokens",
    indices = [(Index(value = ["user_id"], unique = true))]
)
data class Token(
    @ColumnInfo(name = "uid")
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    @ColumnInfo(name = "user_id")
    var userId: String = "",
    @ColumnInfo(name = "time")
    var time: Long = 0L,
    @ColumnInfo(name = "data")
    val data: TokenResponse.Detail
) {
    val auth: String
        get() = "Bearer ${data.accessToken}"

    val isExpired: Boolean
        get() = (System.currentTimeMillis() - time) >= data.expiresIn * 1000
}