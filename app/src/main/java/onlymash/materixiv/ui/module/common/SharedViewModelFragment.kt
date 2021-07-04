package onlymash.materixiv.ui.module.common

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import onlymash.materixiv.R
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.search.SearchActivity

abstract class SharedViewModelFragment : RefreshableListFragment() {

    protected lateinit var sharedViewModel: SharedViewModel

    override fun onCreateViewModel() {
        super.onCreateViewModel()
        sharedViewModel = requireParentFragment().getViewModel()
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        if (activity is SearchActivity) {
            val searchBarHeight = resources.getDimensionPixelSize(R.dimen.searchbar_height) + 2 * resources.getDimensionPixelSize(R.dimen.searchbar_margin_vertical)
            list.updatePadding(top = searchBarHeight)
            swipeRefreshLayout.setProgressViewOffset(false, searchBarHeight, searchBarHeight + swipeRefreshLayout.progressViewEndOffset)
        }
    }

    override fun onTokenLoaded(token: Token) {
        sharedViewModel.updateToken(token)
    }
}