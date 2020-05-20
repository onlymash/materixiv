package onlymash.materixiv.ui.module.common

import android.view.LayoutInflater
import android.view.ViewGroup
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.databinding.FragmentRefreshableListBinding

abstract class RefreshableListFragment : TokenFragment<FragmentRefreshableListBinding>() {

    protected val list get() = binding.refreshableList.list
    protected val progressBarCircular get() = binding.progressCircular.progressBarCircular
    protected val progressBarHorizontal get() = binding.progressHorizontal.progressBarHorizontal
    protected val swipeRefreshLayout get() = binding.refreshableList.swipeRefreshLayout

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRefreshableListBinding {
        return FragmentRefreshableListBinding.inflate(inflater, container, false)
    }

    override fun onLoginStateChange(state: NetworkState?) {

    }

    override fun onRefreshStateChange(state: NetworkState?) {

    }
}