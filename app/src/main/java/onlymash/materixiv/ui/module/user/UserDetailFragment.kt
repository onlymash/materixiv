package onlymash.materixiv.ui.module.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.data.action.Restrict
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.model.UserDetailResponse
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.common.CommonRepositoryImpl
import onlymash.materixiv.data.repository.detail.UserDetailRepositoryImpl
import onlymash.materixiv.databinding.FragmentUserDetailBinding
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.glide.GlideApp
import onlymash.materixiv.ui.module.common.CommonViewModel
import onlymash.materixiv.ui.module.common.SharedViewModel
import onlymash.materixiv.ui.module.common.TokenFragment
import onlymash.materixiv.widget.LinkTransformationMethod
import org.kodein.di.instance

class UserDetailFragment : TokenFragment<FragmentUserDetailBinding>() {

    companion object {
        fun create(userId: String): UserDetailFragment {
            return UserDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(Keys.USER_ID, userId)
                }
            }
        }
    }

    private val pixivAppApi by instance<PixivAppApi>()

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userDetailViewModel: UserDetailViewModel
    private lateinit var commonViewModel: CommonViewModel

    private var auth = ""
    private var myId = ""
    private var userId = ""
    private var userDetail: UserDetailResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            userId = getString(Keys.USER_ID, "")
        }
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserDetailBinding {
        return FragmentUserDetailBinding.inflate(inflater, container, false)
    }

    override fun onCreateViewModel() {
        super.onCreateViewModel()
        sharedViewModel = getViewModel()
        userDetailViewModel= getViewModel(UserDetailViewModel(UserDetailRepositoryImpl(pixivAppApi)))
        commonViewModel = getViewModel(CommonViewModel(CommonRepositoryImpl(pixivAppApi)))
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        userDetailViewModel.userDetail.observe(viewLifecycleOwner, Observer {
            bindData(it)
        })
        userDetailViewModel.isFailed.observe(viewLifecycleOwner, Observer { failed ->
            if (failed) {
                binding.layoutUserInfo.progressBar.isVisible = false
                binding.layoutUserInfo.retryButton.isVisible = true
            }
        })
        binding.layoutUserInfo.retryButton.setOnClickListener {
            binding.layoutUserInfo.retryButton.isVisible = false
            binding.layoutUserInfo.progressBar.isVisible = true
            userDetailViewModel.fetchUserDetail(auth, userId)
        }
        binding.layoutUserInfo.webPage.apply {
            movementMethod = BetterLinkMovementMethod.getInstance()
            transformationMethod = LinkTransformationMethod()
        }
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val visible = appBarLayout.height - binding.toolbar.height - binding.tabs.height + verticalOffset != 0
            binding.layoutUserInfo.userInfoContent.isInvisible = !visible
            binding.toolbarTitleContent.isInvisible = visible
        })
        activity?.menuInflater?.inflate(R.menu.action_user_detail, binding.restrictMenu.menu)
        binding.restrictMenu.setOnMenuItemClickListener { menuItem ->
            handleMenuClick(menuItem.itemId)
            true
        }
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        val clickListener = View.OnClickListener { handleFollow(it, Restrict.PUBLIC) }
        val longClickListener = View.OnLongClickListener {
            handleFollow(it, Restrict.PRIVATE)
            true
        }
        binding.toolbarFollow.setOnClickListener(clickListener)
        binding.layoutUserInfo.follow.setOnClickListener(clickListener)
        binding.toolbarFollow.setOnLongClickListener(longClickListener)
        binding.layoutUserInfo.follow.setOnLongClickListener(longClickListener)
        binding.viewPager.adapter = UserDetailPagerAdapter(userId, this)
        val tabTitles = resources.getStringArray(R.array.user_detail_type_entries)
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
        binding.layoutUserInfo.followingCount.setOnClickListener {
            binding.viewPager.setCurrentItem(4, false)
        }
        binding.layoutUserInfo.friendsCount.setOnClickListener {
            binding.viewPager.setCurrentItem(5, false)
        }
    }

    private fun handleFollow(v: View, restrict: Restrict) {
        if (v != binding.toolbarFollow && v != binding.layoutUserInfo.follow) {
            return
        }
        val user = userDetail?.user ?: return
        val isFollow = !user.isFollowed
        updateFollowState(isFollow)
        lifecycleScope.launch {
            val success = if (isFollow) {
                commonViewModel.addFollowUser(auth, user.id, restrict)
            } else {
                commonViewModel.deleteFollowUser(auth, user.id)
            }
            if (success) {
                userDetail?.user?.isFollowed = isFollow
                userDetailViewModel.updateFollowState(isFollow)
            }
        }
    }

    private fun handleMenuClick(itemId: Int) {
        when (itemId) {
            R.id.action_restrict_public -> sharedViewModel.updateRestrict(Restrict.PUBLIC)
            R.id.action_restrict_private -> sharedViewModel.updateRestrict(Restrict.PRIVATE)
        }
    }

    private fun bindData(userDetail: UserDetailResponse?) {
        this.userDetail = userDetail ?: return
        val userInfoBinding = binding.layoutUserInfo
        userInfoBinding.progressBar.isVisible = false
        val context = context ?: return
        updateFollowState(userDetail.user.isFollowed)
        binding.toolbarTitle.text = userDetail.user.name
        userInfoBinding.name.text = userDetail.user.name
        userInfoBinding.webPage.text = userDetail.profile.webpage ?: userDetail.profile.twitterUrl ?: userDetail.profile.pawooUrl
        userInfoBinding.followingCount.text = getString(R.string.user_detail_following_format, userDetail.profile.totalFollowUsers)
        userInfoBinding.friendsCount.text = getString(R.string.user_detail_friends_format, userDetail.profile.totalMypixivUsers)
        val glide = GlideApp.with(context)
        glide.load(userDetail.user.profileImageUrls.medium)
            .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_avatar))
            .into(userInfoBinding.avatar)
        glide.load(userDetail.user.profileImageUrls.medium)
            .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder_avatar))
            .into(binding.toolbarAvatar)
        if (userDetail.profile.backgroundImageUrl != null) {
            glide.load(userDetail.profile.backgroundImageUrl)
                .into(userInfoBinding.background)
        }
    }

    private fun updateFollowState(isFollowed: Boolean) {
        val textRes = if (isFollowed) R.string.user_following else R.string.user_follow
        binding.toolbarFollow.apply {
            post {
                isChecked = isFollowed
                setText(textRes)
            }
        }
        binding.layoutUserInfo.follow.apply {
            isChecked = isFollowed
            setText(textRes)
        }
    }

    override fun onTokenLoaded(token: Token) {
        val isMe = userId == token.userId
        if (auth.isEmpty() && !isMe) {
            binding.viewPager.setCurrentItem(1, false)
        }
        binding.toolbarFollow.isInvisible = isMe
        binding.layoutUserInfo.follow.isInvisible = isMe
        binding.restrictMenu.isVisible = isMe
        auth = token.auth
        myId = token.userId
        if (userDetail == null) {
            userDetailViewModel.fetchUserDetail(auth, userId)
        }
    }

    override fun onLoginStateChange(state: NetworkState?) {

    }

    override fun onRefreshStateChange(state: NetworkState?) {

    }
}