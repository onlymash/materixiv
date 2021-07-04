package onlymash.materixiv.ui.module.illust

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.chip.Chip
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.dao.DownloadDao
import onlymash.materixiv.data.db.dao.IllustCacheDao
import onlymash.materixiv.data.db.entity.IllustCache
import onlymash.materixiv.data.model.common.Illust
import onlymash.materixiv.data.model.common.Illust.Companion.originUrls
import onlymash.materixiv.data.model.common.Illust.Companion.previewUrls
import onlymash.materixiv.data.model.common.UgoiraMetadata
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.databinding.FragmentIllustDetailBinding
import onlymash.materixiv.extensions.getDownloads
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.extensions.getWindowHeight
import onlymash.materixiv.glide.BlurTransformation
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.base.ViewModelFragment
import onlymash.materixiv.ui.module.comment.CommentDialog
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.StorageFolderLifecycleObserver
import onlymash.materixiv.ui.module.search.SearchActivity
import onlymash.materixiv.ui.module.user.UserDetailActivity
import onlymash.materixiv.utils.DateUtil
import onlymash.materixiv.widget.LinkTransformationMethod
import onlymash.materixiv.worker.DownloadWorker
import org.kodein.di.instance

class IllustDetailFragment : ViewModelFragment<FragmentIllustDetailBinding>() {

    companion object {
        private const val TOKEN_UID_KEY = "token_uid"
        private const val AUTH_KEY = "auth"
        private const val QUERY_KEY = "illust_query"
        fun create(tokenUid: Long, auth: String, id: Long, query: String): IllustDetailFragment {
            return IllustDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(TOKEN_UID_KEY, tokenUid)
                    putString(AUTH_KEY, auth)
                    putLong(Keys.ILLUST_ID, id)
                    putString(QUERY_KEY, query)
                }
            }
        }
    }

    private val sp by instance<SharedPreferences>()
    private val downloadDao by instance<DownloadDao>()
    private val illustDao by instance<IllustCacheDao>()
    private val pixivAppApi by instance<PixivAppApi>()

    private var auth: String = ""
    private var tokenUid: Long = 0
    private var illustId: Long = 0
    private var illust: Illust? = null
    private var query = ""
    private var ugoira: UgoiraMetadata? = null
    private var toDownloadUgoira = false

    private lateinit var adapter: IllustDetailAdapter
    private lateinit var illustDeatilViewModel: IllustDetailViewModel
    private lateinit var commonViewModel: CommonViewModel
    private lateinit var sharedViewModel: IllustDetailSharedViewModel
    private lateinit var observer: StorageFolderLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            tokenUid = getLong(TOKEN_UID_KEY)
            auth = getString(AUTH_KEY, "")
            illustId = getLong(Keys.ILLUST_ID)
            query = getString(QUERY_KEY, "")
        }
        observer = StorageFolderLifecycleObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(observer)
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentIllustDetailBinding {
        return FragmentIllustDetailBinding.inflate(inflater, container, false)
    }

    override fun onCreateViewModel() {
        illustDeatilViewModel = getViewModel(
            IllustDetailViewModel(
                illustCacheDao = illustDao,
                tokenUid = tokenUid,
                query = query,
                illustId = illustId
            )
        )
        commonViewModel = getViewModel(CommonViewModel(CommonRepositoryImpl(pixivAppApi, illustDao)))
        sharedViewModel = requireActivity().getViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.layoutParams.height = requireActivity().getWindowHeight() * 3 / 5
        binding.toolbar.apply {
            setNavigationOnClickListener {
                activity?.onBackPressed()
            }
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_share -> shareLink()
                    R.id.action_download -> download()
                    R.id.action_download_all -> downloadAll()
                    R.id.action_download_ugoira -> downloadUgoira()
                }
                true
            }
        }
        binding.detailContent.userContainer.setOnClickListener { toUserDetailPage() }
        val listener = object : IllustDetailAdapter.ClickItemListener {
            override fun onClickItem(
                view: View,
                position: Int,
                userId: String,
                illustId: Long,
                urls: ArrayList<String>,
                previews: ArrayList<String>
            ) {
                if (activity?.isFinishing == false) {
                    val transaction = childFragmentManager.beginTransaction()
                        .addSharedElement(view, view.transitionName)
                    IllustBrowseDialog.create(userId, illustId, position, urls, previews)
                        .show(transaction, "browse")
                }
            }
        }
        adapter = IllustDetailAdapter(listener)
        sharedViewModel.topWindowSize.observe(viewLifecycleOwner, { systemWindowInsetTop ->
            binding.toolbar.updateLayoutParams<CollapsingToolbarLayout.LayoutParams> {
                topMargin = systemWindowInsetTop
                adapter.imageMarginTop = systemWindowInsetTop + binding.toolbar.minimumHeight
            }
        })
        binding.detailList.adapter = adapter
        val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val pageCount = adapter.itemCount
                if (pageCount > 1) {
                    binding.toolbar.subtitle = "${position + 1}/${pageCount}P"
                }
            }
        }
        binding.detailList.registerOnPageChangeCallback(pageChangeCallback)
        binding.detailList.offscreenPageLimit = 2
        illustDeatilViewModel.illust.observe(viewLifecycleOwner, { illust ->
            if (illust != null) {
                bindData(illust)
            }
        })
        commonViewModel.ugoira.observe(viewLifecycleOwner, { ugoiraMetadata ->
            ugoira = ugoiraMetadata
            if (toDownloadUgoira) {
                downloadUgoira()
            }
        })
        binding.detailContent.chipRelated.setOnClickListener {
            context?.let { context ->
                SearchActivity.startSearch(
                    context = context,
                    type = Values.SEARCH_TYPE_ILLUST,
                    word = "Related: $illustId",
                    illustId = illustId
                )
            }
        }
        binding.detailContent.chipComments.setOnClickListener {
            CommentDialog.create(illustId).show(childFragmentManager, "comment")
        }
    }

    private fun shareLink() {
        val illust = illust ?: return
        val context = context ?: return
        val webUrl = "${Values.BASE_URL}/artworks/${illust.id}"
        context.startActivity(Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, webUrl)
            },
            getString(R.string.common_share_via)
        ))
    }

    private fun downloadUgoira() {
        val illust = illust ?: return
        val ugoira = ugoira
        if (ugoira == null) {
            toDownloadUgoira = true
            commonViewModel.fetchUgoiraMetadata(auth, illustId)
            return
        }
        toDownloadUgoira = false
        val context = context
        val previews = illust.previewUrls
        if (context == null || previews.isNullOrEmpty()) {
            return
        }
        val urls = listOf(ugoira.zipUrls.medium)
        lifecycleScope.launch {
            val downloads = context.getDownloads(
                illustId = illust.id,
                userId = illust.user.id.toString(),
                dirName = "ugoira",
                urls = urls,
                previews = previews
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

    private fun download() {
        val illust = illust ?: return
        val context = context
        val urls = illust.originUrls
        val previews = illust.previewUrls
        if (context == null || urls.isNullOrEmpty() || previews.isNullOrEmpty()) {
            return
        }
        val position = binding.detailList.currentItem
        val preview = previews[position]
        val url = urls[position]
        lifecycleScope.launch {
            val downloads = context.getDownloads(
                illustId = illust.id,
                userId = illust.user.id.toString(),
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
        val illust = illust ?: return
        val context = context
        val urls = illust.originUrls
        val previews = illust.previewUrls
        if (context == null || urls.isNullOrEmpty() || previews.isNullOrEmpty()) {
            return
        }
        lifecycleScope.launch {
            val downloads = context.getDownloads(
                illustId = illust.id,
                userId = illust.user.id.toString(),
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

    private fun toUserDetailPage() {
        val userId = illust?.user?.id ?: return
        val context = context ?: return
        UserDetailActivity.start(context, userId.toString())
    }

    private fun bindData(illustCache: IllustCache) {
        if (this.illust?.id == illustCache.id) {
            binding.fabBookmark.isActivated = illustCache.illust.isBookmarked
            return
        }
        val illust = illustCache.illust
        this.illust = illust
        GlideApp.with(this)
            .load(illust.imageUrls.medium)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageViewBlur)
        adapter.illust = illust
        val context = context ?: return
        val pageCount = illust.metaPages.size
        val menuResId = when {
            illust.isUgoira -> R.menu.toolbar_illust_detail_ugoira
            pageCount > 1 -> {
                binding.toolbar.subtitle = "1/${pageCount}P"
                R.menu.toolbar_illust_detail
            }
            else -> R.menu.toolbar_illust_detail_single
        }
        binding.toolbar.apply {
            title = illust.id.toString()
            menu.clear()
            inflateMenu(menuResId)
        }
        binding.fabBookmark.isActivated = illust.isBookmarked
        binding.fabBookmark.setOnClickListener {
            bookmark(!binding.fabBookmark.isActivated, illustCache)
        }
        binding.fabBookmark.setOnLongClickListener {
            if (!binding.fabBookmark.isActivated) {
                bookmark(true, illustCache, Restrict.PRIVATE)
            }
            true
        }
        val detailBinding = binding.detailContent
        detailBinding.title.text = illust.title
        detailBinding.username.text = illust.user.name
        GlideApp.with(context)
            .load(illust.user.profileImageUrls.medium)
            .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_avatar))
            .into(detailBinding.avatar)
        detailBinding.date.text = DateUtil.formatDate(context, illust.createDate)
        detailBinding.viewsCount.text = getString(R.string.illust_detail_views_format, illust.totalView)
        detailBinding.bookmarksCount.text = getString(R.string.illust_detail_likes_format, illust.totalBookmarks)
        if (illust.caption.isNotBlank()) {
            detailBinding.caption.isVisible = true
            detailBinding.caption.apply {
                movementMethod = BetterLinkMovementMethod.getInstance()
                transformationMethod = LinkTransformationMethod()
            }
            val markwon = Markwon.builder(context)
                .usePlugins(listOf(HtmlPlugin.create(), StrikethroughPlugin.create(), LinkifyPlugin.create(Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS)))
                .build()
            markwon.setMarkdown(detailBinding.caption, illust.caption)
        } else {
            detailBinding.caption.isVisible = false
        }
        detailBinding.tagsGroup.removeAllViews()
        if (illust.tags.isNotEmpty()) {
            detailBinding.tagsGroup.isVisible = true
            illust.tags.forEachIndexed { index, tag ->
                val chip = Chip(context)
                chip.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                chip.id = index
                chip.text = tag.name
                chip.setOnClickListener {
                    SearchActivity.startSearch(context, Values.SEARCH_TYPE_ILLUST, tag.name)
                }
                detailBinding.tagsGroup.addView(chip)
            }
        } else {
            detailBinding.tagsGroup.isVisible = false
        }
    }

    private fun bookmark(isAdd: Boolean, illustCache: IllustCache, restrict: Restrict = Restrict.PUBLIC) {
        if (isAdd) {
            commonViewModel.addBookmarkIllust(illustCache, auth, restrict)
        } else {
            commonViewModel.deleteBookmarkIllust(illustCache, auth)
        }
    }
}