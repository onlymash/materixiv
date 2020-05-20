package onlymash.materixiv.data.db.entity

import androidx.room.*
import onlymash.materixiv.data.model.common.Illust

@Entity(
    tableName = "illusts",
    indices = [(Index(value = ["token_uid", "query", "id"], unique = true))],
    foreignKeys = [(ForeignKey(
        entity = Token::class,
        parentColumns = ["uid"],
        childColumns = ["token_uid"],
        onDelete = ForeignKey.CASCADE))]
)
data class Illustration(
    @ColumnInfo(name = "uid")
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    @ColumnInfo(name = "token_uid")
    val tokenUid: Long,
    @ColumnInfo(name = "query")
    val query: String,
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "index")
    val index: Int,
    @ColumnInfo(name = "illust")
    val illust: Illust
)