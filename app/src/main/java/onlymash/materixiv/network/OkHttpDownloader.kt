package onlymash.materixiv.network

import android.content.ContentResolver
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import okio.IOException
import onlymash.materixiv.app.Keys
import onlymash.materixiv.data.db.entity.Download
import onlymash.materixiv.extensions.copy
import onlymash.materixiv.extensions.safeCloseQuietly
import onlymash.materixiv.network.pixiv.PixivDownloadInterceptor
import onlymash.materixiv.network.pixiv.pixivClientBuilder
import java.io.*
import java.net.SocketTimeoutException

class OkHttpDownloader : Downloader {

    private var listener: DownloadListener? = null

    private val client = pixivClientBuilder
        .addInterceptor(PixivDownloadInterceptor())
        .build()

    override suspend fun download(appContext: Context, download: Download) {
        if (download.isDone) {
            listener?.onSuccess()
            return
        }
        withContext(Dispatchers.IO) {
            val dir = File(appContext.externalCacheDir, download.dirName)
            if (!dir.exists()) {
                dir.mkdirs()
            } else if (dir.isFile) {
                dir.delete()
                dir.mkdirs()
            }
            val file = File(dir, download.fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            download(appContext.contentResolver, download, file)
        }
    }

    private fun download(contentResolver: ContentResolver, download: Download, file: File) {
        val contentLength = getContentLength(download.url)
        if (contentLength == -1L) {
            throw SocketTimeoutException()
        }
        var fileInputStream: InputStream? = null
        var outputStream: OutputStream? = null

        var downloadedSize = file.length()
        if (download.fileSize != contentLength) {
            if (downloadedSize > 0) {
                file.delete()
                file.createNewFile()
            }
            downloadedSize = 0
            listener?.onProgress(0, contentLength)
        } else if (downloadedSize == contentLength) {
            try {
                fileInputStream = FileInputStream(file)
                outputStream = contentResolver.openOutputStream(download.fileUri)
                fileInputStream.copy(outputStream)
                file.delete()
            } catch (_: IOException) {
                fileInputStream?.safeCloseQuietly()
                outputStream?.safeCloseQuietly()
            }
            listener?.onProgress(contentLength, contentLength)
            return
        }

        val request = Request.Builder()
            .url(download.url)
            .addHeader(Keys.RANGE, "bytes=$downloadedSize-")
            .build()
        var httpInputStream: InputStream? = null
        try {
            httpInputStream = client.newCall(request).execute().body?.byteStream()?.also { input ->
                val raf = RandomAccessFile(file, "rwd")
                raf.seek(downloadedSize)
                val buff = ByteArray(1024 * 16)
                var len: Int
                while (input.read(buff).also { len = it } != -1) {
                    raf.write(buff, 0, len)
                    downloadedSize += len
                    listener?.onProgress(downloadedSize, contentLength)
                }
                if (downloadedSize == contentLength) {
                    fileInputStream = FileInputStream(file)
                    outputStream = contentResolver.openOutputStream(download.fileUri)
                    fileInputStream.copy(outputStream)
                    listener?.onSuccess()
                } else {
                    listener?.onFailed()
                }
            }
        } catch (_: IOException) {
            listener?.onFailed()
        } finally {
            fileInputStream?.safeCloseQuietly()
            outputStream?.safeCloseQuietly()
            httpInputStream?.safeCloseQuietly()
        }
    }

    private fun getContentLength(url: String): Long {
        val request = Request.Builder()
            .url(url)
            .build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val contentLength = response.body?.contentLength() ?: -1L
                response.close()
                return if (contentLength == 0L) -1L else contentLength
            }
        } catch (_: IOException) {}
        return -1L
    }

    fun setDownloadListener(listener: DownloadListener) {
        this.listener = listener
    }

    interface DownloadListener {
        fun onProgress(downloadedSize: Long, totalSize: Long)
        fun onFailed()
        fun onSuccess()
    }
}