package onlymash.materixiv.ui.module.common

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import onlymash.materixiv.app.App
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.setValue
import onlymash.materixiv.extensions.toDecodedString
import org.kodein.di.instance

private const val EXTERNAL_STORAGE_PRIMARY_EMULATED_ROOT_ID = "primary"
private const val EXTERNAL_STORAGE_PROVIDER_AUTHORITY = "com.android.externalstorage.documents"

class StorageFolderLifecycleObserver(
    private val registry: ActivityResultRegistry) : DefaultLifecycleObserver {

    private val sp by App.app.instance<SharedPreferences>("sp")
    private lateinit var getDocumentTree : ActivityResultLauncher<Uri?>

    override fun onCreate(owner: LifecycleOwner) {
        getDocumentTree = registry.register("open_document_tree", ActivityResultContracts.OpenDocumentTree()) { uri ->
            persistUri(uri)
        }
    }

    private fun persistUri(uri: Uri?) {
        if (uri == null) {
            return
        }
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        App.app.contentResolver.apply {
            persistedUriPermissions.forEach { permission ->
                if (permission.isWritePermission && permission.uri != uri) {
                    releasePersistableUriPermission(permission.uri, flags)
                }
            }
            takePersistableUriPermission(uri, flags)
        }
        sp.setValue(Keys.STORAGE_FOLDER, uri.toDecodedString())
    }

    fun openDocumentTree(context: Context) {
        getDocumentTree.launch(context.getRootUri())
    }

    private fun Context.getRootUri(): Uri? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val sv = getSystemService(StorageManager::class.java)?.primaryStorageVolume ?: return null
                val rootId = if (sv.isEmulated) {
                    EXTERNAL_STORAGE_PRIMARY_EMULATED_ROOT_ID
                } else {
                    sv.uuid
                } ?: return null
                DocumentsContract.buildRootUri(EXTERNAL_STORAGE_PROVIDER_AUTHORITY, rootId)
            }
            else -> null
        }
    }
}