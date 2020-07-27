package onlymash.materixiv.ui.module.illust

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.data.db.dao.DownloadDao
import onlymash.materixiv.databinding.DialogIllustBrowseBinding
import onlymash.materixiv.databinding.ItemIllustBrowseBinding
import onlymash.materixiv.extensions.*
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.base.BindingDialog
import onlymash.materixiv.ui.module.common.StorageFolderLifecycleObserver
import onlymash.materixiv.ui.viewbinding.viewBinding
import onlymash.materixiv.widget.DismissFrameLayout
import onlymash.materixiv.worker.DownloadWorker
import org.kodein.di.instance
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream

class IllustBrowseDialog : BindingDialog<DialogIllustBrowseBinding>(), DismissFrameLayout.OnDismissListener {

    companion object {
        private const val ALPHA_MAX = 0xFF
        private const val ALPHA_MIN = 0x00
        private const val URLS_KEY = "urls"
        private const val PREVIEW_URLS_KEY = "preview_urls"
        private const val ILLUST_ID_KEY = "illust_id"
        private const val POSITION_KEY = "position"
        fun create(
            userId: String,
            illustId: Long,
            position: Int,
            urls: ArrayList<String>,
            previews: ArrayList<String>
        ): IllustBrowseDialog {
            return IllustBrowseDialog().apply {
                arguments = Bundle().apply {
                    putString(Keys.USER_ID, userId)
                    putLong(ILLUST_ID_KEY, illustId)
                    putInt(POSITION_KEY, position)
                    putStringArrayList(URLS_KEY, urls)
                    putStringArrayList(PREVIEW_URLS_KEY, previews)
                }
            }
        }
    }

    private val sp by instance<SharedPreferences>()
    private val downloadDao by instance<DownloadDao>()

    private val urls: ArrayList<String> = arrayListOf()
    private val previews: ArrayList<String> = arrayListOf()
    private var userId = "0"
    private var illustId: Long = 0
    private var position = 0
    private lateinit var colorDrawable: ColorDrawable
    private lateinit var observer: StorageFolderLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogThemeFullScreen_Transparent)
        arguments?.apply {
            userId = getString(Keys.USER_ID, "0")
            illustId = getLong(ILLUST_ID_KEY, 0)
            position = getInt(POSITION_KEY, 0)
            urls.addAll(getStringArrayList(URLS_KEY) ?: arrayListOf())
            previews.addAll(getStringArrayList(PREVIEW_URLS_KEY) ?: arrayListOf())
        }
        observer = StorageFolderLifecycleObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(observer)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogIllustBrowseBinding {
        postponeEnterTransition()
        return DialogIllustBrowseBinding.inflate(inflater, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.isFullscreen = false
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorDrawable = ColorDrawable(ContextCompat.getColor(view.context, R.color.black))
        view.background = colorDrawable
        val styledAttributes = requireContext().theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        view.setOnApplyWindowInsetsListener { _, insets ->
            binding.appBar.minimumHeight = actionBarHeight + insets.systemWindowInsetTop
            binding.appBar.updatePadding(
                top = insets.systemWindowInsetTop,
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight
            )
            binding.bottomShortcut.updateLayoutParams<FrameLayout.LayoutParams> {
                updateMargins(
                    bottom = insets.systemWindowInsetBottom,
                    left = insets.systemWindowInsetLeft,
                    right = insets.systemWindowInsetRight
                )
            }
            insets
        }
        val count = urls.size
        binding.toolbar.apply {
            inflateMenu(R.menu.toolbar_browse_dialog)
            setNavigationOnClickListener {
                dismiss()
            }
            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_set_as) {
                    setAs()
                }
                true
            }
            title = illustId.toString()
            if (count > 1) {
                subtitle = "${position + 1}/${count}P"
            }
        }
        binding.viewPager.adapter = IllustBrowseAdapter(urls, this) {
            val window = dialog?.window
            if (window != null) {
                val visible = !binding.appBar.isVisible
                window.isFullscreen = !visible
                binding.appBar.isVisible = visible
                binding.bottomShortcut.isVisible = visible
                binding.shadow.isVisible = visible
            }
        }
        binding.viewPager.setCurrentItem(position, false)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (count > 1) {
                    binding.toolbar.subtitle = "${position + 1}/${count}P"
                }
            }
        })
        setupBottomShortcut()
        startPostponedEnterTransition()
    }

    private fun setupBottomShortcut() {
        binding.download.setupTooltipText()
        binding.downloadAll.setupTooltipText()
        binding.share.setupTooltipText()
        binding.download.setOnClickListener { download() }
        binding.downloadAll.setOnClickListener { downloadAll() }
        binding.share.setOnClickListener { share() }
    }

    private fun download() {
        val context = context
        if (context == null || urls.isEmpty() || previews.size != urls.size) {
            return
        }
        lifecycleScope.launch {
            val position = binding.viewPager.currentItem
            val url = urls[position]
            val preview = previews[position]
            val downloads = context.getDownloads(
                illustId = illustId,
                userId = userId,
                dirName = "image",
                urls = listOf(url),
                previews = listOf(preview)
            ) {
                observer.openDocumentTree(context, sp)
            }
            if (!downloads.isNullOrEmpty()) {
                withContext(Dispatchers.IO) {
                    DownloadWorker.runWork(context.applicationContext, downloadDao.insert(downloads[0]))
                }
            }
        }
    }

    private fun downloadAll() {
        val context = context
        if (context == null || urls.isEmpty() || previews.size != urls.size) {
            return
        }
        lifecycleScope.launch {
            val downloads = context.getDownloads(
                illustId = illustId,
                userId = userId,
                dirName = "image",
                urls = urls,
                previews = previews
            ) {
                observer.openDocumentTree(context, sp)
            }
            if (!downloads.isNullOrEmpty()) {
                withContext(Dispatchers.IO) {
                    DownloadWorker.runWorks(context.applicationContext, downloadDao.insert(downloads))
                }
            }
        }
    }

    private fun setAs() {
        val context = context ?: return
        val url = urls[binding.viewPager.currentItem]
        lifecycleScope.launch {
            val file = getFile(context, url)
            if (file != null) {
                val cacheFile = File(context.externalCacheDir, url.fileName())
                val desUri = cacheFile.toUri()
                if (context.copyFile(file, desUri)) {
                    startActivity(Intent.createChooser(
                        Intent(Intent.ACTION_ATTACH_DATA).apply {
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            putExtra(Intent.EXTRA_MIME_TYPES, url.getMimeType())
                            data = context.getUriForFile(cacheFile)
                        },
                        getString(R.string.common_share_via)
                    ))
                }
            }
        }
    }

    private suspend fun Context.copyFile(file: File, desUri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            var inputStream: InputStream? = null
            var outputSteam: OutputStream? = null
            try {
                inputStream = FileInputStream(file)
                outputSteam = contentResolver.openOutputStream(desUri)
                inputStream.copy(outputSteam)
                true
            } catch (_: IOException) {
                false
            } finally {
                inputStream?.safeCloseQuietly()
                outputSteam?.safeCloseQuietly()
            }
        }
    }

    private fun share() {
        val context = context ?: return
        val url = urls[binding.viewPager.currentItem]
        lifecycleScope.launch {
            val file = getFile(context, url)
            if (file != null) {
                val uri = context.getUriForFile(file)
                context.startActivity(Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        type = url.getMimeType()
                        putExtra(Intent.EXTRA_STREAM, uri)
                    },
                    getString(R.string.common_share_via)
                ))
            }
        }
    }

    private suspend fun getFile(context: Context, url: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                GlideApp.with(context)
                    .downloadOnly()
                    .load(url)
                    .submit()
                    .get()
            } catch (_: Exception) {
                null
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            dismiss()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDismissStart() {
        colorDrawable.alpha = ALPHA_MIN
    }

    override fun onDismissProgress(progress: Float) {

    }

    override fun onDismiss() {
        dismiss()
    }

    override fun onDismissCancel() {
        colorDrawable.alpha = ALPHA_MAX
    }

    class IllustBrowseAdapter(
        private val urls: ArrayList<String>,
        private val dismissCallback: DismissFrameLayout.OnDismissListener,
        private val clickImageCallback: () -> Unit
    ) : RecyclerView.Adapter<IllustBrowseAdapter.IllustBrowseViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IllustBrowseViewHolder {
            return IllustBrowseViewHolder(parent)
        }

        override fun onBindViewHolder(holder: IllustBrowseViewHolder, position: Int) {
            holder.bind(urls[position])
        }

        override fun getItemCount(): Int {
            return urls.size
        }

        inner class IllustBrowseViewHolder(binding: ItemIllustBrowseBinding): RecyclerView.ViewHolder(binding.root) {

            constructor(parent: ViewGroup): this(parent.viewBinding(ItemIllustBrowseBinding::inflate))

            private val container = binding.itemContainer
            private val photoView = binding.photoView
            private val progressBar = binding.progressBar
            private val retryButton = binding.retryButton
            private var url: String? = null

            init {
                container.setDismissListener(dismissCallback)
                photoView.setOnViewTapListener { _, _, _ ->
                    clickImageCallback.invoke()
                }
                retryButton.setOnClickListener {
                    url?.let {
                        bind(it)
                    }
                }
            }

            fun bind(url: String) {
                this.url = url
                photoView.transitionName = "image_$layoutPosition"
                retryButton.isVisible = false
                progressBar.isVisible = true
                val listener = object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        retryButton.isVisible = true
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }
                }
                GlideApp.with(itemView.context)
                    .load(url)
                    .addListener(listener)
                    .into(photoView)
            }
        }
    }
}