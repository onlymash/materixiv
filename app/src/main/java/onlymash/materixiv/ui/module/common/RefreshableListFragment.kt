package onlymash.materixiv.ui.module.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import onlymash.materixiv.databinding.FragmentRefreshableListBinding

abstract class RefreshableListFragment : TokenFragment<FragmentRefreshableListBinding>() {

    protected lateinit var list: RecyclerView
    protected lateinit var progressBarHorizontal: ProgressBar
    protected lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRefreshableListBinding {
        return FragmentRefreshableListBinding.inflate(inflater, container, false)
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        list = binding.refreshableList.list
        progressBarHorizontal = binding.progressHorizontal.progressBarHorizontal
        swipeRefreshLayout = binding.refreshableList.swipeRefreshLayout
    }

    fun jumpToListTop() {
        list.scrollToPosition(0)
    }
}