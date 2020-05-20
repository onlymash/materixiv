package onlymash.materixiv.data.db.entity

import android.net.Uri
import android.provider.DocumentsContract
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import onlymash.materixiv.extensions.getDocumentFileId

@Entity(
    tableName = "downloads",
    indices = [(Index(value = ["url"], unique = true))]
)
data class Download(
    @ColumnInfo(name = "uid")
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "preview_url")
    val previewUrl: String,
    @ColumnInfo(name = "authority")
    val authority: String,
    @ColumnInfo(name = "tree_id")
    var treeId: String,
    @ColumnInfo(name = "dir_name")
    var dirName: String,
    @ColumnInfo(name = "file_name")
    var fileName: String,
    @ColumnInfo(name = "file_size")
    var fileSize: Long = -1,
    @ColumnInfo(name = "downloaded_size")
    var downloadedSize: Long = -1
) {
    val fileUri: Uri
        get() {
            val fileId = getDocumentFileId(getDocumentFileId(treeId, dirName), fileName)
            val treeUri = DocumentsContract.buildTreeDocumentUri(authority, treeId)
            return DocumentsContract.buildDocumentUriUsingTree(treeUri, fileId)
        }

    val isDone: Boolean
        get() = downloadedSize > 0 && downloadedSize == fileSize
}