package onlymash.materixiv.network

import android.content.Context
import onlymash.materixiv.data.db.entity.Download

interface Downloader {
    suspend fun download(appContext: Context, download: Download)
}