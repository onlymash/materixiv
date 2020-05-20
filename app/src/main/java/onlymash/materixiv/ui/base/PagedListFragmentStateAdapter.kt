package onlymash.materixiv.ui.base

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.AsyncPagedListDiffer.PagedListListener
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter

@Suppress("LeakingThis")
abstract class PagedListFragmentStateAdapter<T>(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    diffCallback: DiffUtil.ItemCallback<T>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val listener = PagedListListener { previousList: PagedList<T>?, currentList: PagedList<T>? ->
        onCurrentListChanged(previousList, currentList)
    }

    private val differ: AsyncPagedListDiffer<T> = AsyncPagedListDiffer(this, diffCallback)

    init {
        differ.addPagedListListener(listener)
    }

    val currentList: PagedList<T>?
        get() = differ.currentList

    fun submitList(pagedList: PagedList<T>?) {
        differ.submitList(pagedList)
    }

    fun submitList(pagedList: PagedList<T>?, commitCallback: Runnable?) {
        differ.submitList(pagedList, commitCallback)
    }

    fun getItem(position: Int): T? {
        return differ.getItem(position)
    }

    override fun getItemCount(): Int {
        return differ.itemCount
    }

    open fun onCurrentListChanged(previousList: PagedList<T>?, currentList: PagedList<T>?) {
        
    }
}