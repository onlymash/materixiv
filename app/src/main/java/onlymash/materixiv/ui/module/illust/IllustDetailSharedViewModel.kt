package onlymash.materixiv.ui.module.illust

import androidx.core.graphics.Insets
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IllustDetailSharedViewModel : ViewModel() {

    private val _insets = MutableLiveData<Insets>()
    val insets = _insets

    fun updateInsets(insets: Insets) {
        if (_insets.value != insets) {
            _insets.postValue(insets)
        }
    }
}