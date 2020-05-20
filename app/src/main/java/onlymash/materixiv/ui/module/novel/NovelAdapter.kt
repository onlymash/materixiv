package onlymash.materixiv.ui.module.novel

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import onlymash.materixiv.R
import onlymash.materixiv.data.model.common.Novel
import onlymash.materixiv.databinding.ItemNovelBinding
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.base.FooterPagedAdapter
import onlymash.materixiv.ui.viewbinding.viewBinding

class NovelAdapter(
    retryCallback: () -> Unit
) : FooterPagedAdapter<Novel>(NOVEL_COMPARATOR, retryCallback) {
    companion object {
        val NOVEL_COMPARATOR = object : DiffUtil.ItemCallback<Novel>() {
            override fun areContentsTheSame(
                oldItem: Novel,
                newItem: Novel
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(
                oldItem: Novel,
                newItem: Novel
            ): Boolean = oldItem.id == newItem.id
        }
    }

    override fun onCreateItemViewHolder(
        parent: ViewGroup,
        viewType: Int): RecyclerView.ViewHolder = NovelViewHolder(parent)

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NovelViewHolder).bind(getSafeItem(position))
    }

    inner class NovelViewHolder(binding: ItemNovelBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup): this(parent.viewBinding(ItemNovelBinding::inflate))

        private val cover = binding.cover
        private val title = binding.title
        private val seriesTitle = binding.seriesTitle
        private val author = binding.author
        private val tags = binding.tags

        private var novel: Novel? = null

        fun bind(novel: Novel?) {
            this.novel = novel ?: return
            GlideApp.with(itemView.context)
                .load(novel.imageUrls.medium)
                .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.placeholder_background))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(cover)
            title.text = novel.title
            seriesTitle.text = novel.series?.title
            seriesTitle.isVisible = !seriesTitle.text.isNullOrEmpty()
            author.text = novel.user.name
            var tagsString = ""
            novel.tags.forEach { tag ->
                tagsString = "$tagsString ${tag.name}"
            }
            tagsString = tagsString.trim()
            tags.text = tagsString
        }
    }
}