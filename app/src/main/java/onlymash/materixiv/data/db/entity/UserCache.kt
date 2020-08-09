package onlymash.materixiv.data.db.entity

import androidx.room.*
import onlymash.materixiv.data.model.common.UserPreview

@Entity(
    tableName = "users",
    indices = [(Index(value = ["token_uid", "query", "id"], unique = true))],
    foreignKeys = [(ForeignKey(
        entity = Token::class,
        parentColumns = ["uid"],
        childColumns = ["token_uid"],
        onDelete = ForeignKey.CASCADE))]
)
data class UserCache(
    @ColumnInfo(name = "uid")
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    @ColumnInfo(name = "token_uid")
    val tokenUid: Long,
    @ColumnInfo(name = "query")
    val query: String,
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "next_url")
    val nextUrl: String? = null,
    @ColumnInfo(name = "user_preview")
    val userPreview: UserPreview
)