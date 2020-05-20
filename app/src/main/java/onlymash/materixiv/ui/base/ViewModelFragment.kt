package onlymash.materixiv.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class ViewModelFragment<T: ViewBinding> : BindingFragment<T>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onCreateViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun onCreateViewModel()
}