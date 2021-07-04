package onlymash.materixiv.ui.module.user

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import onlymash.materixiv.R
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.db.entity.UserCache
import onlymash.materixiv.databinding.ItemUserBinding
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.module.illust.IllustDeatilActivity
import onlymash.materixiv.ui.viewbinding.viewBinding

class UserAdapter(
    private val followCallback: (UserCache, Restrict) -> Unit
) : PagingDataAdapter<UserCache, UserAdapter.UserViewHolder>(USER_PREVIEW_COMPARATOR) {
    companion object {
        val USER_PREVIEW_COMPARATOR = object : DiffUtil.ItemCallback<UserCache>() {
            override fun areContentsTheSame(
                oldItem: UserCache,
                newItem: UserCache
            ): Boolean = oldItem.id == newItem.id && oldItem.userPreview.user.isFollowed == newItem.userPreview.user.isFollowed

            override fun areItemsTheSame(
                oldItem: UserCache,
                newItem: UserCache
            ): Boolean = oldItem.id == newItem.id
        }
    }

    fun withLoadStateFooterSafe(
        footer: LoadStateAdapter<*>
    ): ConcatAdapter {
        val containerAdapter = ConcatAdapter(this)
        addLoadStateListener { loadStates ->
            footer.loadState = loadStates.append
            if (loadStates.append is LoadState.Error && !containerAdapter.adapters.contains(footer)) {
                containerAdapter.addAdapter(footer)
                footer.loadState = loadStates.append
            } else if (containerAdapter.adapters.contains(footer)){
                containerAdapter.removeAdapter(footer)
            }
        }
        return containerAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(parent)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup): this(parent.viewBinding(ItemUserBinding::inflate))

        private val preview0 = binding.preview0
        private val preview1 = binding.preview1
        private val preview2 = binding.preview2
        private val avatar = binding.avatar
        private val name = binding.name
        private val follow = binding.follow
        private var userCache: UserCache? = null

        init {
            itemView.setOnClickListener {
                userCache?.userPreview?.user?.id?.let { userId ->
                    UserDetailActivity.start(itemView.context, userId.toString())
                }
            }
            preview0.setOnClickListener {
                userCache?.userPreview?.illusts?.let { illusts ->
                    if (illusts.isNotEmpty()) {
                        IllustDeatilActivity.start(itemView.context, illusts[0].id)
                    }
                }
            }
            preview1.setOnClickListener {
                userCache?.userPreview?.illusts?.let { illusts ->
                    if (illusts.size >= 2) {
                        IllustDeatilActivity.start(itemView.context, illusts[1].id)
                    }
                }
            }
            preview2.setOnClickListener {
                userCache?.userPreview?.illusts?.let { illusts ->
                    if (illusts.size >= 3) {
                        IllustDeatilActivity.start(itemView.context, illusts[2].id)
                    }
                }
            }
            follow.setOnClickListener {
                userCache?.let { user ->
                    setFollowState(!user.userPreview.user.isFollowed)
                    followCallback.invoke(user, Restrict.PUBLIC)
                }
            }
            follow.setOnLongClickListener {
                userCache?.let { user ->
                    val isFollowed = !user.userPreview.user.isFollowed
                    if (isFollowed) {
                        setFollowState(isFollowed)
                        followCallback.invoke(user, Restrict.PRIVATE)
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

        fun bind(userCache: UserCache?) {
            this.userCache = userCache ?: return
            val preview = userCache.userPreview
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