package onlymash.materixiv.ui.base

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import onlymash.materixiv.R
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.isFailed
import onlymash.materixiv.databinding.ItemFooterBinding
import onlymash.materixiv.ui.viewbinding.viewBinding
import java.lang.IndexOutOfBoundsException

abstract class FooterPagedAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val retryCallback: () -> Unit
) : PagedListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    fun getSafeItem(position: Int): T? {
        return try {
            getItem(position)
        } catch (_: IndexOutOfBoundsException) {
            null
        }
    }

    private var networkState: NetworkState? = null

    private fun hasExtraRow() = networkState.isFailed()

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_footer
        } else {
            super.getItemViewType(position)
        }
    }

    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_footer -> FooterViewHolder(parent)
            else -> onCreateItemViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_footer -> {
                val layoutParams = holder.itemView.layoutParams
                if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                    layoutParams.isFullSpan = true
                }
                (holder as FooterPagedAdapter<*>.FooterViewHolder).bind()
            }
            else -> {
                onBindItemViewHolder(holder, position)
            }
        }
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && itemCount > 0) {
            notifyItemChanged(itemCount - 1)
        }
    }

    inner class FooterViewHolder(binding: ItemFooterBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.viewBinding(ItemFooterBinding::inflate))

        private val errorMsg = binding.errorMsg

        init {
            binding.retryButton.setOnClickListener {
                retryCallback.invoke()
            }
        }

        fun bind() {
            errorMsg.text = networkState?.msg
        }
    }
}