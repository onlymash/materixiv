package onlymash.materixiv.ui.module.illust

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IllustDetailSharedViewModel : ViewModel() {

    val topWindowSize = MutableLiveData<Int>()

    fun updateTopSize(size: Int) {
        if (topWindowSize.value != size) {
            topWindowSize.postValue(size)
        }
    }
}