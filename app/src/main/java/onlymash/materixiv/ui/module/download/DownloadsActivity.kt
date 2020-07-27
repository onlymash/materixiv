package onlymash.materixiv.ui.module.download

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.work.WorkManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import onlymash.materixiv.R
import onlymash.materixiv.data.db.dao.DownloadDao
import onlymash.materixiv.data.db.entity.Download
import onlymash.materixiv.databinding.ActivityDownloadsBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.base.KodeinActivity
import onlymash.materixiv.ui.helper.ItemTouchCallback
import onlymash.materixiv.ui.helper.ItemTouchHelperCallback
import onlymash.materixiv.ui.viewbinding.viewBinding
import org.kodein.di.instance

class DownloadsActivity : KodeinActivity() {

    private val downloadDao by instance<DownloadDao>()
    private val binding by viewBinding(ActivityDownloadsBinding::inflate)
    private lateinit var viewModel: DownloadViewModel
    private lateinit var adapter: DownloadsAdapter
    private lateinit var job: Job
    private var updated = false
    private var downloads: List<Download>? = null

    private val itemTouchCAllback = object : ItemTouchCallback {
        override val isDragEnabled: Boolean
            get() = false
        override val isSwipeEnabled: Boolean
            get() = true

        override fun onDragItem(position: Int, targetPosition: Int) {}

        override fun onSwipeItem(position: Int) {
            val downloads = downloads
            if (downloads != null && downloads.size > position) {
                viewModel.delete(downloads[position].uid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.title_downloads)
        }
        adapter = DownloadsAdapter(WorkManager.getInstance(applicationContext), menuInflater)
        binding.list.adapter = adapter
        ItemTouchHelper(ItemTouchHelperCallback(itemTouchCAllback))
            .attachToRecyclerView(binding.list)
        viewModel = getViewModel(DownloadViewModel(downloadDao))
        viewModel.downloads.observe(this, Observer {
            downloads = it
            updated = true
        })
        viewModel.loadAll()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_downloads, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_clear_all -> handleDeleteAll()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleDeleteAll() {
        if (isFinishing) {
            return
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.download_clear_all)
            .setMessage(R.string.download_clear_all_tip)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.deleteAll()
            }
            .setNegativeButton(R.string.dialog_no, null)
            .create()
            .show()
    }

    override fun onResume() {
        super.onResume()
        job = lifecycleScope.launch {
            val ticker = ticker(delayMillis = 500, initialDelayMillis = 200)
            for (event in ticker) {
                if (updated) {
                    updated = false
                    val data = downloads ?: listOf()
                    adapter.updateData(data)
                    binding.progressBar.isVisible = false
                    data.forEachIndexed { index, download ->
                        val holder = binding.list.findViewHolderForAdapterPosition(index)
                        if (holder is DownloadsAdapter.DownloadViewHolder) {
                            holder.bindProgress(download)
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }
}