package onlymash.materixiv.receiver

import android.content.*
import android.net.Uri
import androidx.work.Data
import onlymash.materixiv.R
import onlymash.materixiv.extensions.getMimeType
import onlymash.materixiv.worker.DownloadWorker

class DownloadNotificationClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        val uri = intent.data
        val data = intent.getByteArrayExtra(DownloadWorker.INPUT_DATA_KEY)
        when {
            uri != null -> openUri(context, uri)
            data != null -> DownloadWorker.runWork(context.applicationContext, Data.fromByteArray(data))
        }
    }

    private fun openUri(context: Context, uri: Uri) {
        if (uri.scheme != ContentResolver.SCHEME_CONTENT) {
            return
        }
        val newIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            setDataAndType(uri, uri.toString().getMimeType())
        }
        try {
            context.startActivity(
                Intent.createChooser(
                    newIntent,
                    context.getString(R.string.common_share_via)
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: ActivityNotFoundException) {

        } catch (_: RuntimeException) {

        }
    }
}