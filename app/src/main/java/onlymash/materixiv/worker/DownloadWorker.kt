package onlymash.materixiv.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.*
import onlymash.materixiv.app.App
import onlymash.materixiv.data.db.dao.DownloadDao
import onlymash.materixiv.network.OkHttpDownloader
import org.kodein.di.instance
import onlymash.materixiv.R
import onlymash.materixiv.receiver.DownloadNotificationClickReceiver
import java.util.concurrent.TimeUnit

class DownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val INPUT_DATA_KEY = "input_data"
        private const val DOWNLOAD_UID_KEY = "download_uid"
        private const val NOTIFICATION_ELAPSED_TIME = 1000L
        private const val DELAY = 2L

        fun runWork(appContext: Context, downloadUid: Long) {
            val data = workDataOf(DOWNLOAD_UID_KEY to downloadUid)
            runWork(appContext, data)
        }

        fun runWork(appContext: Context, data: Data) {
            val uid = data.getLong(DOWNLOAD_UID_KEY, -1)
            if (uid < 0) {
                return
            }
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .addTag(uid.toString())
                .setInitialDelay(DELAY, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(appContext).enqueue(request)
        }

        fun runWorks(appContext: Context, downloadUids: List<Long>) {
            if (downloadUids.isEmpty()) {
                return
            }
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val requests = downloadUids.map { uid ->
                OneTimeWorkRequestBuilder<DownloadWorker>()
                    .setInputData(workDataOf(DOWNLOAD_UID_KEY to uid))
                    .setConstraints(constraints)
                    .addTag(uid.toString())
                    .setInitialDelay(DELAY, TimeUnit.SECONDS)
                    .build()
            }
            if (requests.size == 1) {
                WorkManager.getInstance(appContext).enqueue(requests)
            } else {
                var continuation = WorkManager.getInstance(appContext).beginWith(requests[0])
                for (i in 1 until requests.size) {
                    continuation = continuation.then(requests[i])
                }
                continuation.enqueue()
            }
        }
    }

    override suspend fun doWork(): Result {
        val uid = inputData.getLong(DOWNLOAD_UID_KEY, -1L)
        if (uid < 0L) {
            return Result.failure()
        }
        val app = applicationContext as App
        val downloadDao by app.instance<DownloadDao>()
        val download = downloadDao.getDownload(uid) ?: return Result.failure()
        val downloader = OkHttpDownloader()
        var success = false
        val channelId = applicationContext.packageName + ":download"
        val channelName = applicationContext.getString(R.string.common_download)
        val title = "${download.dirName} - ${download.fileName}"
        val notificationManager = getNotificationManager(channelId, channelName)
        val downloadingBuilder = getDownloadingNotificationBuilder(
            channelId = channelId,
            title = title,
            url = download.url
        )
        var startTime = 0L
        var elapsedTime = NOTIFICATION_ELAPSED_TIME
        downloader.setDownloadListener(object : OkHttpDownloader.DownloadListener {
            override fun onProgress(downloadedSize: Long, totalSize: Long) {
                download.downloadedSize = downloadedSize
                download.fileSize = totalSize
                downloadDao.update(download)
                if (elapsedTime - startTime >= NOTIFICATION_ELAPSED_TIME) {
                    startTime = System.currentTimeMillis()
                    elapsedTime = 0L
                    val progress = (downloadedSize * 100 / totalSize).toInt()
                    val notify = downloadingBuilder.setProgress(100, progress, false).build()
                    notificationManager?.notify(download.uid.toInt(), notify)
                } else {
                    elapsedTime = System.currentTimeMillis()
                }
            }
            override fun onFailed() {
                success = false
            }
            override fun onSuccess() {
                success = true
            }
        })
        try {
            downloader.download(app, download)
        } catch (_: Exception) {
            success = false
        }
        val notify = if (success) {
            getDownloadedNotificationBuilder(title, channelId, download.fileUri).build()
        } else {
            getDownloadErrorNotificationBuilder(title, channelId).build()
        }
        notificationManager?.notify(download.uid.toInt(), notify)
        return if (success) Result.success() else Result.failure()
    }

    private fun getNotificationManager(channelId: String, channelName: String): NotificationManager? {
        val notificationManager = applicationContext.getSystemService<NotificationManager>() ?: return null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        return notificationManager
    }

    private fun getDownloadingNotificationBuilder(title: String, url: String, channelId: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setContentTitle(title)
            .setContentText(url)
            .setOngoing(true)
            .setAutoCancel(false)
            .setShowWhen(false)
    }

    private fun getDownloadedNotificationBuilder(title: String, channelId: String, desUri: Uri): NotificationCompat.Builder {
        val intent = Intent(applicationContext, DownloadNotificationClickReceiver::class.java)
        intent.data = desUri
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT)
        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText(applicationContext.getString(R.string.msg_download_complete))
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }

    private fun getDownloadErrorNotificationBuilder(title: String, channelId: String): NotificationCompat.Builder {
        val intent = Intent(applicationContext, DownloadNotificationClickReceiver::class.java)
        intent.putExtra(INPUT_DATA_KEY, inputData.toByteArray())
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle(title)
            .setContentText(applicationContext.getString(R.string.msg_download_failed))
            .setOngoing(false)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.stat_sys_download,
                applicationContext.getString(R.string.button_retry),
                pendingIntent
            )
    }
}