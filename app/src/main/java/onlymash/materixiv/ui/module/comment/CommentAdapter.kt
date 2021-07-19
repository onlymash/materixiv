package onlymash.materixiv.ui.module.comment

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import onlymash.materixiv.R
import onlymash.materixiv.data.model.common.Comment
import onlymash.materixiv.databinding.ItemCommentBinding
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.module.user.UserDetailActivity
import onlymash.materixiv.ui.viewbinding.viewBinding
import onlymash.materixiv.utils.DateUtil

class CommentAdapter : PagingDataAdapter<Comment, CommentAdapter.CommentViewHolder>(COMMENT_COMPARATOR) {

    companion object {
        val COMMENT_COMPARATOR = object : DiffUtil.ItemCallback<Comment>() {
            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(parent)
    }

    class CommentViewHolder(binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        constructor(parent: ViewGroup) : this(parent.viewBinding(ItemCommentBinding::inflate))
        private val avatar = binding.avatar
        private val username = binding.username
        private val comment = binding.comment
        private val date = binding.date
        private val reply = binding.reply

        private var data: Comment? = null

        init {
            avatar.setOnClickListener {
                openUser()
            }
            username.setOnClickListener {
                openUser()
            }
        }

        private fun openUser() {
            data?.let {
                UserDetailActivity.start(itemView.context, userId = it.user.id.toString())
            }
        }

        fun bind(data: Comment?) {
            this.data = data ?: return
            val context = itemView.context
            GlideApp.with(context)
                .load(data.user.profileImageUrls.medium)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_avatar))
                .into(avatar)
            username.text = data.user.name
            comment.text = data.comment
            date.text = DateUtil.formatDate(context, data.date)
        }
    }
}