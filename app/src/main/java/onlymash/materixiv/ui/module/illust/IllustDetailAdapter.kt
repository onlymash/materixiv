package onlymash.materixiv.ui.module.illust

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import onlymash.materixiv.R
import onlymash.materixiv.data.model.common.Illust
import onlymash.materixiv.data.model.common.Illust.Companion.originUrls
import onlymash.materixiv.data.model.common.Illust.Companion.previewUrls
import onlymash.materixiv.databinding.ItemIllustDetailBinding
import onlymash.materixiv.glide.BlurTransformation
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.glide.RoundedCornersTransformation
import onlymash.materixiv.ui.viewbinding.viewBinding

class IllustDetailAdapter(
    private val listener: ClickItemListener
) : RecyclerView.Adapter<IllustDetailAdapter.IllustDetailViewHolder>() {

    interface ClickItemListener {
        fun onClickItem(
            view: View,
            position: Int,
            userId: String,
            illustId: Long,
            urls: ArrayList<String>,
            previews: ArrayList<String>
        )
    }

    var imageMarginTop = 0

    private var _illust: Illust? = null

    var illust: Illust
        get() = _illust!!
        set(value) {
            _illust = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IllustDetailViewHolder {
        return IllustDetailViewHolder(parent)
    }

    override fun onBindViewHolder(holder: IllustDetailViewHolder, position: Int) {
        val url = if (illust.metaPages.isEmpty()) {
            illust.imageUrls.large
        } else {
            illust.metaPages[position].imageUrls.large
        }
        holder.bind(url)
    }

    override fun getItemCount(): Int {
        if (_illust == null) {
            return 0
        }
        if (illust.metaPages.isEmpty()) {
            return 1
        }
        return illust.metaPages.size
    }

    inner class IllustDetailViewHolder(binding: ItemIllustDetailBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.viewBinding(ItemIllustDetailBinding::inflate))

        private val imageView = binding.imageView
        private val imageViewBlur = binding.imageViewBlur
        private val progressBar = binding.progressBar

        init {
            itemView.setOnClickListener {
                listener.onClickItem(
                    view = imageView,
                    position = layoutPosition,
                    userId = illust.user.id.toString(),
                    illustId = illust.id,
                    urls = illust.originUrls,
                    previews = illust.previewUrls
                )
            }
            imageView.updateLayoutParams<FrameLayout.LayoutParams> {
                topMargin = imageMarginTop
            }
        }

        fun bind(url: String) {
            imageView.transitionName = "image_$layoutPosition"
            val listener = object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.isVisible = false
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
            val glide = GlideApp.with(itemView)
            glide.load(url)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageViewBlur)
            val radius = itemView.resources.getDimensionPixelSize(R.dimen.spacing_10dp)
            glide.load(url)
                .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(radius, 0)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .addListener(listener)
                .into(imageView)
        }
    }
}