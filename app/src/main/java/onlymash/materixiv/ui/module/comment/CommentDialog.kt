package onlymash.materixiv.ui.module.comment

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import onlymash.materixiv.app.Keys
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.comment.CommentRepositoryImpl
import onlymash.materixiv.data.repository.isFailed
import onlymash.materixiv.data.repository.isRunning
import onlymash.materixiv.databinding.DialogCommentBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.TokenBottomSheetDialog
import org.kodein.di.instance

class CommentDialog : TokenBottomSheetDialog<DialogCommentBinding>() {

    companion object {
        fun create(illustId: Long): CommentDialog {
            return CommentDialog().apply {
                arguments = Bundle().apply {
                    putLong(Keys.ILLUST_ID, illustId)
                }
            }
        }
    }

    private val pixivAppApi by instance<PixivAppApi>()

    private var illustId: Long = 0
    private var token: Token? = null
    private lateinit var commentViewModel: CommentViewModel
    private lateinit var commentApapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        illustId = arguments?.getLong(Keys.ILLUST_ID) ?: 0
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogCommentBinding {
        return DialogCommentBinding.inflate(inflater, container, false)
    }

    override fun onCreateViewModel() {
        super.onCreateViewModel()
        commentViewModel = getViewModel(CommentViewModel(
            illustId = illustId,
            repo = CommentRepositoryImpl(pixivAppApi)
        ))
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.minimumHeight = getWindowHeight() / 2
        commentApapter = CommentAdapter()
        binding.commentList.adapter = commentApapter
        commentViewModel.comments.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.size > 0) {
                    binding.progressBar.isVisible = false
                }
                commentApapter.submitList(it)
            }
        })
        commentViewModel.networkState.observe(viewLifecycleOwner, Observer {
            if (it.isFailed()) {
                binding.progressBar.isVisible = false
                binding.retryButton.isVisible = true
            } else if (it.isRunning() && commentApapter.itemCount == 0) {
                binding.retryButton.isVisible = false
                binding.progressBar.isVisible = true
            } else {
                binding.retryButton.isVisible = false
                binding.progressBar.isVisible = false
            }
        })
        commentViewModel.refreshState.observe(viewLifecycleOwner, Observer {

        })
        binding.retryButton.setOnClickListener {
            commentViewModel.retry()
        }
    }

    override fun onTokenLoaded(token: Token) {
        this.token = token
        commentViewModel.show(token.auth)
    }

    override fun onLoginStateChange(state: NetworkState?) {

    }

    override fun onRefreshStateChange(state: NetworkState?) {

    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}