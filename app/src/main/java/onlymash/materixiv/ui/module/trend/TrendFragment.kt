package onlymash.materixiv.ui.module.trend

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.trend.TrendRepositoryImpl
import onlymash.materixiv.databinding.FragmentTrendBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.extensions.getWindowWidth
import onlymash.materixiv.ui.module.common.TokenFragment
import onlymash.materixiv.ui.module.common.SharedViewModel
import org.kodein.di.instance
import kotlin.math.roundToInt

class TrendFragment : TokenFragment<FragmentTrendBinding>() {

    companion object {
        fun create(type: Int): TrendFragment {
            return TrendFragment().apply {
                arguments = Bundle().apply {
                    putInt(Keys.PAGE_TYPE, type)
                }
            }
        }
    }

    private val list get() = binding.refreshableList.list
    private val progressBarCircular get() = binding.progressCircular.progressBarCircular
    private val swipeRefreshLayout get() = binding.refreshableList.swipeRefreshLayout
    private val retryButton get() = binding.retryButton

    private val pixivAppApi by instance<PixivAppApi>()
    private var type = Values.SEARCH_TYPE_ILLUST

    private lateinit var trendAdapter: TrendAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var trendViewModel: TrendViewModel

    private val spanCount: Int
        get() = activity?.getSanCount() ?: 3

    private fun Activity.getSanCount(): Int {
        val itemWidth = resources.getDimensionPixelSize(R.dimen.trend_item_width)
        val count = (getWindowWidth().toFloat() / itemWidth.toFloat()).roundToInt()
        return if (count < 1) 1 else count
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getInt(Keys.PAGE_TYPE) ?: Values.SEARCH_TYPE_ILLUST
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrendBinding {
        return FragmentTrendBinding.inflate(inflater, container, false)
    }
    override fun onCreateViewModel() {
        super.onCreateViewModel()
        sharedViewModel = requireParentFragment().getViewModel()
        trendViewModel = getViewModel(TrendViewModel(TrendRepositoryImpl(pixivAppApi)))
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        trendAdapter = TrendAdapter(type)
        list.apply {
            updatePadding(left = 0, right = 0)
            layoutManager = StaggeredGridLayoutManager(spanCount, RecyclerView.VERTICAL)
            adapter = trendAdapter
        }
        trendViewModel.trendTags.observe(viewLifecycleOwner, Observer { trendTags ->
            if (trendTags == null) {
                if (trendAdapter.trendTags.isEmpty()) {
                    retryButton.isVisible = true
                }
            } else {
                retryButton.isVisible = false
                trendAdapter.trendTags = trendTags
            }
        })
        trendViewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                retryButton.isVisible = false
            }
            progressBarCircular.isVisible = isLoading && trendAdapter.itemCount == 0
        })
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        retryButton.setOnClickListener {
            refresh()
        }
    }

    override fun onTokenLoaded(token: Token) {
        sharedViewModel.updateToken(token)
        trendViewModel.fetchTrendTags(token.auth, type)
    }

    fun refresh() {
        val auth = sharedViewModel.token.value?.auth ?: return
        trendViewModel.fetchTrendTags(auth, type)
    }

    override fun onLoginStateChange(state: NetworkState?) { }

    override fun onRefreshStateChange(state: NetworkState?) { }
}