package onlymash.materixiv.ui.module.illust

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import onlymash.materixiv.data.db.entity.IllustCache
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.ui.base.PagingFragmentStateAdapter

class IllustDetailPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : PagingFragmentStateAdapter<IllustCache>(fragmentManager, lifecycle, ILLUST_DETAIL_COMPARATOR) {

    companion object {
        val ILLUST_DETAIL_COMPARATOR = object : DiffUtil.ItemCallback<IllustCache>() {
            override fun areContentsTheSame(oldItem: IllustCache, newItem: IllustCache): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areItemsTheSame(oldItem: IllustCache, newItem: IllustCache): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    private var _token: Token? = null
    var token: Token
        get() = _token!!
        set(value) {
            _token = value
        }

    var query: String = ""

    override fun createFragment(position: Int): Fragment {
        return IllustDetailFragment.create(
            tokenUid = token.uid,
            auth = token.auth,
            id = getItem(position)?.id ?: 0,
            query = query
        )
    }

    override fun getItemCount(): Int {
        if (_token == null) {
            return 0
        }
        return super.getItemCount()
    }
}