package onlymash.materixiv.ui.module.novel

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import onlymash.materixiv.R
import onlymash.materixiv.data.model.common.Novel
import onlymash.materixiv.databinding.ItemNovelBinding
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.viewbinding.viewBinding
import onlymash.materixiv.utils.DateUtil

class NovelViewHolder(binding: ItemNovelBinding) : RecyclerView.ViewHolder(binding.root) {

    constructor(parent: ViewGroup): this(parent.viewBinding(ItemNovelBinding::inflate))

    private val cover = binding.cover
    private val title = binding.title
    private val seriesTitle = binding.seriesTitle
    private val author = binding.author
    private val tags = binding.tags
    private val date = binding.date

    private var novel: Novel? = null

    init {
        itemView.setOnClickListener {
            novel?.apply {
                NovelReaderActivity.startActivity(context = itemView.context, novelId = id)
            }
        }
    }

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
        date.text = DateUtil.formatDate(itemView.context, novel.createDate)
    }
}