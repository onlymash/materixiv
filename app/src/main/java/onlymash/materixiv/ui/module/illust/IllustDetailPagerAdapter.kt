package onlymash.materixiv.ui.module.illust

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import onlymash.materixiv.data.db.entity.Illustration
import onlymash.materixiv.ui.base.PagedListFragmentStateAdapter

class IllustDetailPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : PagedListFragmentStateAdapter<Illustration>(fragmentManager, lifecycle, ILLUST_DETAIL_COMPARATOR) {

    companion object {
        val ILLUST_DETAIL_COMPARATOR = object : DiffUtil.ItemCallback<Illustration>() {
            override fun areContentsTheSame(oldItem: Illustration, newItem: Illustration): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areItemsTheSame(oldItem: Illustration, newItem: Illustration): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    private var tokenUid: Long = 0
    private var auth: String = ""
    private var query: String = ""

    override fun createFragment(position: Int): Fragment {
        return IllustDetailFragment.create(
            tokenUid = tokenUid,
            auth = auth,
            id = getItem(position)?.id ?: 0,
            query = query
        )
    }

    fun submitData(
        lists: PagedList<Illustration>,
        tokenUid: Long,
        auth: String,
        query: String,
        commitCallback: () -> Unit
    ) {
        this.tokenUid = tokenUid
        this.auth = auth
        this.query = query
        submitList(lists, Runnable { commitCallback.invoke() })
    }
}