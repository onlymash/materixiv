package onlymash.materixiv.ui.module.trend

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.textview.MaterialTextView
import onlymash.materixiv.R
import onlymash.materixiv.data.model.common.TrendTag
import onlymash.materixiv.databinding.ItemTrendBinding
import onlymash.materixiv.databinding.ItemTrendHeaderBinding
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.module.search.SearchActivity
import onlymash.materixiv.ui.viewbinding.viewBinding

@SuppressLint("NotifyDataSetChanged")
class TrendAdapter(private val type: Int) : RecyclerView.Adapter<TrendAdapter.BaseTrendViewHolder>() {

    companion object {
        private const val TREND_ITEM_TYPE_HEADER = 0
        private const val TREND_ITEM_TYPE_NORMAL = 1
    }

    var trendTags: List<TrendTag> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseTrendViewHolder {
        return when (viewType) {
            TREND_ITEM_TYPE_HEADER -> TrendHeaderViewHolder(type, parent)
            else -> TrendViewHolder(type, parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TREND_ITEM_TYPE_HEADER else TREND_ITEM_TYPE_NORMAL
    }

    override fun getItemCount(): Int = trendTags.size

    override fun onBindViewHolder(holder: BaseTrendViewHolder, position: Int) {
        if (position == 0) {
            holder.itemView.updateLayoutParams<StaggeredGridLayoutManager.LayoutParams> {
                isFullSpan = true
            }
        }
        holder.bind(trendTag = trendTags[position])
    }


    abstract class BaseTrendViewHolder(private val type: Int, itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var tag = ""

        abstract val preview: AppCompatImageView
        abstract val nameJp: MaterialTextView
        abstract val nameTranslation: MaterialTextView

        init {
            itemView.setOnClickListener {
                SearchActivity.startSearch(itemView.context, type, tag)
            }
        }

        fun bind(trendTag: TrendTag) {
            tag = trendTag.tag
            nameJp.text = String.format("#%s", trendTag.tag)
            nameTranslation.text = trendTag.translatedName
            val url = when (this) {
                is TrendHeaderViewHolder -> trendTag.illust.imageUrls.medium
                else -> trendTag.illust.imageUrls.squareMedium
            }
            GlideApp.with(itemView.context)
                .load(url)
                .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.placeholder_background))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(preview)
        }
    }

    class TrendViewHolder(type: Int, binding: ItemTrendBinding) : BaseTrendViewHolder(type, binding.root) {
        constructor(type: Int, parent: ViewGroup): this(type, parent.viewBinding(ItemTrendBinding::inflate))
        override val preview = binding.preview
        override val nameJp = binding.nameJp
        override val nameTranslation = binding.nameTranslation
    }

    class TrendHeaderViewHolder(type: Int, binding: ItemTrendHeaderBinding) : BaseTrendViewHolder(type, binding.root) {
        constructor(type: Int, parent: ViewGroup): this(type, parent.viewBinding(ItemTrendHeaderBinding::inflate))
        override val preview = binding.preview
        override val nameJp = binding.nameJp
        override val nameTranslation = binding.nameTranslation
    }
}