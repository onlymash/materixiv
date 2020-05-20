package onlymash.materixiv.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

inline fun <reified M : ViewModel> ViewModelStoreOwner.getViewModel(viewModelFactory: ViewModelProvider.Factory): M {
    return ViewModelProvider(this, viewModelFactory).get(M::class.java)
}

inline fun <reified M : ViewModel> ViewModelStoreOwner.getViewModel(viewModel: ViewModel): M {
    return getViewModel(object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return viewModel as T
        }
    })
}

inline fun <reified M : ViewModel> ViewModelStoreOwner.getViewModel(): M {
    return ViewModelProvider(this).get(M::class.java)
}