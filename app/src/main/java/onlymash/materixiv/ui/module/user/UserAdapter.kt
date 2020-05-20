package onlymash.materixiv.ui.module.user

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import onlymash.materixiv.R
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.model.common.UserPreview
import onlymash.materixiv.databinding.ItemUserBinding
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.base.FooterPagedAdapter
import onlymash.materixiv.ui.module.illust.IllustDeatilActivity
import onlymash.materixiv.ui.viewbinding.viewBinding

class UserAdapter(
    private val followCallback: (Long, Boolean, Restrict) -> Unit,
    retryCallback: () -> Unit
) : FooterPagedAdapter<UserPreview>(USER_PREVIEW_COMPARATOR, retryCallback) {
    companion object {
        val USER_PREVIEW_COMPARATOR = object : DiffUtil.ItemCallback<UserPreview>() {
            override fun areContentsTheSame(
                oldItem: UserPreview,
                newItem: UserPreview
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(
                oldItem: UserPreview,
                newItem: UserPreview
            ): Boolean = oldItem.user.id == newItem.user.id
        }
    }

    override fun onCreateItemViewHolder(
        parent: ViewGroup,
        viewType: Int): RecyclerView.ViewHolder = UserViewHolder(parent)

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserViewHolder).bind(getSafeItem(position))
    }

    inner class UserViewHolder(binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup): this(parent.viewBinding(ItemUserBinding::inflate))

        private val preview0 = binding.preview0
        private val preview1 = binding.preview1
        private val preview2 = binding.preview2
        private val avatar = binding.avatar
        private val name = binding.name
        private val follow = binding.follow
        private var userPreview: UserPreview? = null

        init {
            itemView.setOnClickListener {
                userPreview?.user?.id?.let { userId ->
                    UserDetailActivity.start(itemView.context, userId.toString())
                }
            }
            preview0.setOnClickListener {
                userPreview?.illusts?.let { illusts ->
                    if (illusts.isNotEmpty()) {
                        IllustDeatilActivity.start(itemView.context, illusts[0].id)
                    }
                }
            }
            preview1.setOnClickListener {
                userPreview?.illusts?.let { illusts ->
                    if (illusts.size >= 2) {
                        IllustDeatilActivity.start(itemView.context, illusts[1].id)
                    }
                }
            }
            preview2.setOnClickListener {
                userPreview?.illusts?.let { illusts ->
                    if (illusts.size >= 3) {
                        IllustDeatilActivity.start(itemView.context, illusts[2].id)
                    }
                }
            }
            follow.setOnClickListener {
                userPreview?.let {
                    val isFollowed = !it.user.isFollowed
                    val userId = it.user.id
                    setFollowState(isFollowed)
                    followCallback.invoke(userId, isFollowed, Restrict.PUBLIC)
                }
            }
            follow.setOnLongClickListener {
                userPreview?.let {
                    val isFollowed = !it.user.isFollowed
                    if (isFollowed) {
                        val userId = it.user.id
                        setFollowState(isFollowed)
                        followCallback.invoke(userId, isFollowed, Restrict.PRIVATE)
                    }
                }
                true
            }
        }

        private fun setFollowState(isFollowed: Boolean) {
            if (isFollowed) {
                follow.text = itemView.context.getString(R.string.user_following)
                follow.isChecked = true
            } else {
                follow.text = itemView.context.getString(R.string.user_follow)
                follow.isChecked = false
            }
        }

        fun bind(preview: UserPreview?) {
            userPreview = preview ?: return
            val context = itemView.context
            name.text = preview.user.name
            setFollowState(preview.user.isFollowed)
            GlideApp.with(context)
                .load(preview.user.profileImageUrls.medium)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_avatar))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(avatar)
            preview.illusts.forEachIndexed { index, illust ->
                if (index <= 2) {
                    val view = when (index) {
                        0 -> preview0
                        1 -> preview1
                        else -> preview2
                    }
                    GlideApp.with(context)
                        .load(illust.imageUrls.medium)
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_background))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(view)
                }
            }
        }
    }
}