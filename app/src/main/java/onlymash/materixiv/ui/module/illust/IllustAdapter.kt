package onlymash.materixiv.ui.module.illust

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import onlymash.materixiv.R
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.databinding.ItemIllustBinding
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.base.FooterPagedAdapter
import onlymash.materixiv.ui.viewbinding.viewBinding

class IllustAdapter(
    private val bookmarkCallback: (Boolean, Illustration) -> Unit,
    retryCallback: () -> Unit
) : FooterPagedAdapter<Illustration>(ILLUST_COMPARATOR, retryCallback){

    companion object {
        val ILLUST_COMPARATOR = object : DiffUtil.ItemCallback<Illustration>() {
            override fun areContentsTheSame(
                    oldItem: Illustration,
                    newItem: Illustration
            ): Boolean = oldItem.id == newItem.id &&
                    oldItem.illust.isBookmarked == newItem.illust.isBookmarked

            override fun areItemsTheSame(
                    oldItem: Illustration,
                    newItem: Illustration
            ): Boolean = oldItem.id == newItem.id
        }
    }

    override fun onCreateItemViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = IllustViewHolder(parent)

    override fun onBindItemViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as IllustViewHolder).bindView(getSafeItem(position))
    }

    inner class IllustViewHolder(binding: ItemIllustBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup): this(parent.viewBinding(ItemIllustBinding::inflate))

        private var illustration: Illustration? = null
        private val preview = binding.illustPreview
        private val count = binding.count
        private val bookmark = binding.bookmark

        init {
            itemView.setOnClickListener {
                illustration?.let { illust ->
                    IllustDeatilActivity.start(itemView.context, illust.query, layoutPosition)
                }
            }
            bookmark.setOnClickListener {
                illustration?.let { illust ->
                    val isAdd = !bookmark.isActivated
                    bookmarkCallback.invoke(isAdd, illust)
                    bookmark.isActivated = isAdd
                }
            }
        }

        fun bindView(illustration: Illustration?) {
            this.illustration = illustration ?: return
            val illust = illustration.illust
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