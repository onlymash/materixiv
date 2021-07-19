package onlymash.materixiv.ui.paging

import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter


inline fun <reified M: PagingDataAdapter<*, *>> M.withLoadStateFooterSafe(
    footer: LoadStateAdapter<*>
): ConcatAdapter {
    val containerAdapter = ConcatAdapter(this)
    addLoadStateListener { loadStates ->
        footer.loadState = loadStates.append
        if (loadStates.append is LoadState.Error && !containerAdapter.adapters.contains(footer)) {
            containerAdapter.addAdapter(footer)
            footer.loadState = loadStates.append
        } else if (containerAdapter.adapters.contains(footer)){
            containerAdapter.removeAdapter(footer)
        }
    }
    return containerAdapter
}