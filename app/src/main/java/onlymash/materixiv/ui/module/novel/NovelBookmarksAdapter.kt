package onlymash.materixiv.ui.module.novel

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import onlymash.materixiv.data.model.NovelMarkerResponse.MarkedNovel

class NovelBookmarksAdapter
    : PagingDataAdapter<MarkedNovel, NovelViewHolder>(NOVEL_MARKER_COMPARATOR) {

    companion object {
        val NOVEL_MARKER_COMPARATOR = object : DiffUtil.ItemCallback<MarkedNovel>() {
            override fun areContentsTheSame(
                oldItem: MarkedNovel,
                newItem: MarkedNovel
            ): Boolean = oldItem.novel == newItem.novel

            override fun areItemsTheSame(
                oldItem: MarkedNovel,
                newItem: MarkedNovel
            ): Boolean = oldItem.novel.id == newItem.novel.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NovelViewHolder {
        return NovelViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NovelViewHolder, position: Int) {
        holder.bind(getItem(position)?.novel)
    }
}