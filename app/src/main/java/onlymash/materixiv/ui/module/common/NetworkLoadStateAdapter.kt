package onlymash.materixiv.ui.module.common

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import onlymash.materixiv.databinding.ItemFooterBinding
import onlymash.materixiv.ui.viewbinding.viewBinding

class NetworkLoadStateAdapter(
        private val retryCallback: () -> Unit
) : LoadStateAdapter<NetworkLoadStateAdapter.NetworkStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): NetworkStateViewHolder {
        return NetworkStateViewHolder(parent, retryCallback)
    }

    override fun onBindViewHolder(holder: NetworkStateViewHolder, loadState: LoadState) {
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            layoutParams.isFullSpan = true
        }
        holder.bindTo(loadState)
    }

    class NetworkStateViewHolder(binding: ItemFooterBinding, retryCallback: () -> Unit) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup, retryCallback: () -> Unit) : this(
                parent.viewBinding(ItemFooterBinding::inflate),
                retryCallback
        )

        private val errorMsg = binding.errorMsg
        private val retryButton = binding.retryButton

        init {
            retryButton.setOnClickListener { retryCallback.invoke() }
        }

        fun bindTo(loadState: LoadState) {
            retryButton.isVisible = loadState is LoadState.Error
            errorMsg.isVisible = !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
            errorMsg.text = (loadState as? LoadState.Error)?.error?.message
        }
    }
}