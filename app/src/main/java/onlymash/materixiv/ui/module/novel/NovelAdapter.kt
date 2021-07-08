package onlymash.materixiv.ui.module.novel

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import onlymash.materixiv.data.model.common.Novel

class NovelAdapter : PagingDataAdapter<Novel, NovelViewHolder>(NOVEL_COMPARATOR) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NovelViewHolder {
        return NovelViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NovelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}