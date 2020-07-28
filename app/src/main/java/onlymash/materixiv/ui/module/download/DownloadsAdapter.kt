package onlymash.materixiv.ui.module.download

import android.content.ActivityNotFoundException
import android.content.Intent
import android.text.format.Formatter
import android.view.MenuInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import androidx.work.WorkManager
import onlymash.materixiv.R
import onlymash.materixiv.data.db.entity.Download
import onlymash.materixiv.databinding.ItemDownloadBinding
import onlymash.materixiv.extensions.getMimeType
import onlymash.materixiv.extensions.launchUrl
import onlymash.materixiv.extensions.toDecodedString
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.module.illust.IllustDeatilActivity
import onlymash.materixiv.ui.viewbinding.viewBinding
import onlymash.materixiv.worker.DownloadWorker

class DownloadsAdapter(
    private val workManager: WorkManager,
    private val menuInflater: MenuInflater
): RecyclerView.Adapter<DownloadsAdapter.DownloadViewHolder>() {

    companion object {
        private fun diffCallback(oldItems: List<Download>, newItems: List<Download>) = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldItems.size
            }
            override fun getNewListSize(): Int {
                return newItems.size
            }
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].uid == newItems[newItemPosition].uid
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].uid == newItems[newItemPosition].uid
            }
        }
    }

    private val items: MutableList<Download> = mutableListOf()

    val completedDownloads: List<Download>
        get() {
            val newItems: MutableList<Download> = mutableListOf()
            items.forEach { download ->
                if (download.isDone) {
                    newItems.add(download)
                }
            }
            return newItems
        }

    val failedDownloadUids: List<Long>
        get() {
            val uids: MutableList<Long> = mutableListOf()
            items.forEach { download ->
                val workInfo = workManager.getWorkInfosByTag(download.uid.toString()).get()
                if (!workInfo.isNullOrEmpty() && workInfo.last().state == WorkInfo.State.FAILED) {
                    uids.add(download.uid)
                }
            }
            return uids
        }

    fun updateData(downloads: List<Download>) {
        val result = DiffUtil.calculateDiff(diffCallback(items, downloads))
        items.clear()
        items.addAll(downloads)
        result.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        return DownloadViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class DownloadViewHolder(binding: ItemDownloadBinding) : RecyclerView.ViewHolder(binding.root) {
        constructor(parent: ViewGroup) : this(parent.viewBinding(ItemDownloadBinding::inflate))

        private val preview = binding.preview
        private val progressBar = binding.progressBar
        private val title = binding.title
        private val fileSize = binding.fileSize
        private val actionButton = binding.actionButton
        private val menuView = binding.actionMenu

        private var download: Download? = null

        init {
            itemView.setOnClickListener {
                download?.id?.let { id ->
                    IllustDeatilActivity.start(itemView.context, id)
                }
            }
            actionButton.setOnClickListener {
                download?.uid?.let { uid ->
                    DownloadWorker.runWork(itemView.context.applicationContext, uid)
                }
            }
            progressBar.max = 100
            menuView.setOnMenuItemClickListener { menuItem ->
                download?.fileUri?.let { uri ->
                    when (menuItem.itemId) {
                        R.id.action_share -> {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                putExtra(Intent.EXTRA_STREAM, uri)
                                type = uri.toDecodedString().getMimeType()
                            }
                            val context = itemView.context
                            context.startActivity(Intent.createChooser(intent, context.getString(R.string.common_share_via)))
                        }
                        R.id.action_to_video -> {
                            val context = itemView.context
                            val intent = Intent().apply {
                                data = uri
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                setClassName("onlymash.ugoiratovideo", "onlymash.ugoiratovideo.MainActivity")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (_: ActivityNotFoundException) {
                                context.launchUrl("https://play.google.com/store/apps/details?id=onlymash.ugoiratovideo")
                            }
                        }
                    }
                }
                true
            }
        }

        fun bind(download: Download) {
            this.download = download
            title.text = download.fileName
            bindProgress(download)
            GlideApp.with(itemView.context)
                .load(download.previewUrl)
                .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.placeholder_background))
                .into(preview)
        }

        fun bindProgress(download: Download) {
            val total = download.fileSize
            val downloaded = download.downloadedSize
            var process = 0
            if (total > 0 && downloaded >= 0) {
                process = (downloaded * 100 / total).toInt()
                progressBar.progress = process
                fileSize.text = String.format("%s/%s", Formatter.formatFileSize(itemView.context, downloaded), Formatter.formatFileSize(itemView.context, total))
            } else {
                fileSize.text = ""
                progressBar.progress = 0
            }
            val textResId = if (process == 100) {
                R.string.download_state_succeeded
            } else {
                val worksInfo = workManager.getWorkInfosByTag(download.uid.toString()).get()
                if (!worksInfo.isNullOrEmpty()) {
                    when (worksInfo.last().state) {
                        WorkInfo.State.BLOCKED -> R.string.download_state_blocked
                        WorkInfo.State.CANCELLED -> R.string.download_state_cancelled
                        WorkInfo.State.RUNNING -> R.string.download_state_running
                        WorkInfo.State.FAILED -> R.string.download_state_failed
                        WorkInfo.State.ENQUEUED -> R.string.download_state_enqueued
                        WorkInfo.State.SUCCEEDED -> R.string.download_state_succeeded
                    }
                } else {
                    R.string.download_state_unknown
                }
            }
            actionButton.setText(textResId)
            menuView.menu.clear()
            if (R.string.download_state_succeeded == textResId) {
                menuView.isVisible = true
                val menuResId = if (download.fileName.endsWith(".zip")) {
                    R.menu.action_item_download_ugoira
                } else {
                    R.menu.action_item_download
                }
                menuInflater.inflate(menuResId, menuView.menu)
            } else {
                menuView.isVisible = false
            }
        }
    }
}