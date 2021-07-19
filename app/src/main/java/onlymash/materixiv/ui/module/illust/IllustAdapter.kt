package onlymash.materixiv.ui.module.illust

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import onlymash.materixiv.R
import onlymash.materixiv.data.db.entity.IllustCache
import onlymash.materixiv.databinding.ItemIllustBinding
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.viewbinding.viewBinding

class IllustAdapter(
    private val bookmarkCallback: (Boolean, IllustCache) -> Unit
) : PagingDataAdapter<IllustCache, IllustAdapter.IllustViewHolder>(ILLUST_COMPARATOR){

    companion object {
        val ILLUST_COMPARATOR = object : DiffUtil.ItemCallback<IllustCache>() {
            override fun areContentsTheSame(
                    oldItem: IllustCache,
                    newItem: IllustCache
            ): Boolean = oldItem.id == newItem.id &&
                    oldItem.illust.isBookmarked == newItem.illust.isBookmarked

            override fun areItemsTheSame(
                    oldItem: IllustCache,
                    newItem: IllustCache
            ): Boolean = oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IllustViewHolder {
        return IllustViewHolder(parent)
    }

    override fun onBindViewHolder(holder: IllustViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class IllustViewHolder(binding: ItemIllustBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup): this(parent.viewBinding(ItemIllustBinding::inflate))

        private var illustCache: IllustCache? = null
        private val preview = binding.illustPreview
        private val count = binding.count
        private val bookmark = binding.bookmark

        init {
            itemView.setOnClickListener {
                illustCache?.let { illust ->
                    IllustDeatilActivity.start(itemView.context, illust.query, layoutPosition)
                }
            }
            bookmark.setOnClickListener {
                illustCache?.let { illust ->
                    val isAdd = !bookmark.isActivated
                    bookmarkCallback.invoke(isAdd, illust)
                    bookmark.isActivated = isAdd
                }
            }
        }

        fun bindView(illustCache: IllustCache?) {
            this.illustCache = illustCache ?: return
            val illust = illustCache.illust
            val pageCount = illust.pageCount
            count.text = String.format("%dP", pageCount)
            count.isVisible = pageCount > 1
            bookmark.isActivated = illust.isBookmarked
            preview.updateLayoutParams<ConstraintLayout.LayoutParams> {
                dimensionRatio = "${illust.width}:${illust.height}"
            }
            preview.transitionName = "illust_${illust.id}"
            val placeholder = ContextCompat.getDrawable(itemView.context, R.drawable.placeholder_background)
            GlideApp.with(preview)
                .load(illust.imageUrls.medium)
                .placeholder(placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(preview)
        }
    }
}