package onlymash.materixiv.extensions

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlymash.materixiv.R
import onlymash.materixiv.data.db.entity.Download
import java.io.File
import java.util.*

fun Context.getUriForFile(file: File): Uri {
    return FileProvider.getUriForFile(
        this,
        applicationContext.packageName + ".fileprovider",
        file
    )
}

suspend fun Context.getDownloads(
    illustId: Long,
    dirName: String,
    urls: List<String>,
    previews: List<String>,
    openTreeCallback: () -> Unit): List<Download>? {

    val treeUri = contentResolver.getPersistedUri()
    val treeAuthority = treeUri?.authority
    if (treeUri == null || treeAuthority == null) {
        openTreeCallback.invoke()
        return null
    }
    val treeDir = DocumentFile.fromTreeUri(this, treeUri)
    if (treeDir == null || !treeDir.exists() || treeDir.isFile ||
        !treeDir.canRead() || !treeDir.canWrite()) {
        Toast.makeText(this, getString(R.string.msg_path_denied), Toast.LENGTH_LONG).show()
        openTreeCallback.invoke()
        return null
    }
    val treeId = DocumentsContract.getTreeDocumentId(treeUri)
    val dirId = getDocumentFileId(treeId, dirName)
    val dirUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, dirId)
    val dir = DocumentFile.fromSingleUri(this, dirUri)
    try {
        if (dir == null || !dir.exists()) {
            treeDir.createDirectory(dirName)?.uri ?: return null
        } else if (dir.isFile) {
            dir.delete()
            treeDir.createDirectory(dirName)?.uri ?: return null
        }
    } catch (_: Exception) { }
    return withContext(Dispatchers.IO) {
        val downloads: MutableList<Download> = mutableListOf()
        urls.forEachIndexed { index, url ->
            val fileName = url.fileName()
            val fileId= getDocumentFileId(dirId, fileName)
            val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, fileId)
            val file = DocumentFile.fromSingleUri(this@getDownloads, fileUri)
            try {
                if (file == null || !file.exists()) {
                    DocumentsContract.createDocument(
                        contentResolver,
                        dirUri,
                        fileName.getMimeType(),
                        fileName
                    )
                } else if (file.isDirectory) {
                    file.delete()
                    DocumentsContract.createDocument(
                        contentResolver,
                        dirUri,
                        fileName.getMimeType(),
                        fileName
                    )
                }
            } catch (_: Exception) { }
            downloads.add(
                Download(
                    id = illustId,
                    previewUrl = previews[index],
                    url = url,
                    authority = treeAuthority,
                    treeId = treeId,
                    dirName = dirName,
                    fileName = fileName
                )
            )
        }
        downloads
    }
}

fun ContentResolver.getPersistedUri(): Uri? {
    val permissions = persistedUriPermissions
    val index = permissions.indexOfFirst { permission ->
        permission.isReadPermission && permission.isWritePermission
    }
    if (index < 0) {
        return null
    }
    return permissions[index].uri
}

fun getDocumentFileId(prentId: String, fileName: String): String {
    return if (prentId.endsWith(":")) {
        prentId + fileName
    } else {
        "$prentId/$fileName"
    }
}

fun String.getMimeType(): String {
    var extension = fileExt()
    extension = extension.toLowerCase(Locale.getDefault())
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
}

fun String.fileExt(): String {
    val start = lastIndexOf('.') + 1
    val end = indexOfFirst { it == '?' }
    if (start == 0) {
        return ""
    }
    return if (end > start) {
        substring(start, end)
    } else {
        substring(start)
    }
}

fun String.fileName(): String {
    val start = lastIndexOf('/') + 1
    val end = indexOfFirst { it == '?' }
    val encodeFileName = if (end > start) {
        substring(start, end)
    } else {
        substring(start)
    }
    return encodeFileName.toDecodedString()
        .replace("?", "")
        .replace("!", "")
        .replace(":", "_")
        .replace("\"","_")
}

fun Uri.toDecodedString(): String = toString().toDecodedString()

fun String.toDecodedString(): String = Uri.decode(this)