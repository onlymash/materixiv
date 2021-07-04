package onlymash.materixiv.ui.module.search

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.data.model.common.Tag
import onlymash.materixiv.data.repository.NetworkState
import onlymash.materixiv.data.repository.autocomplete.AutocompleteRepositoryImpl
import onlymash.materixiv.extensions.getViewModel
import onlymash.materixiv.ui.module.common.TokenDialog
import org.kodein.di.instance

abstract class BaseSearchDialog<T: ViewBinding> : TokenDialog<T>() {

    private val pixivAppApi by instance<PixivAppApi>()
    private var auth: String? = null
    private lateinit var autocompleteViewModel: AutocompleteViewModel

    override fun onCreateViewModel() {
        super.onCreateViewModel()
        autocompleteViewModel = getViewModel(
            AutocompleteViewModel(
                AutocompleteRepositoryImpl(pixivAppApi)
            )
        )
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        autocompleteViewModel.tags.observe(viewLifecycleOwner, {
            onTagsUpdated(it)
        })
        autocompleteViewModel.names.observe(viewLifecycleOwner, {
            onNamesUpdated(it)
        })
    }

    override fun onTokenLoaded(token: Token) {
        auth = token.auth
    }

    protected fun fetchTags(word: String?) {
        val auth = auth ?: return
        autocompleteViewModel.fetchTags(auth = auth, word = word)
    }

    protected fun fetchNames(word: String?) {
        val auth = auth ?: return
        autocompleteViewModel.fetchUsers(auth = auth, word = word)
    }

    abstract fun onTagsUpdated(tags: List<Tag>)

    abstract fun onNamesUpdated(names: List<String>)

    override fun onLoginStateChange(state: NetworkState?) {}

    override fun onRefreshStateChange(state: NetworkState?) {}
}