package onlymash.materixiv.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
    return PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
    val liveData = MutableLiveData<NetworkState>()
    addListener(object : PagingRequestHelper.Listener {
        override fun onStatusChange(report: PagingRequestHelper.StatusReport) {
            when {
                report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
                report.hasError() -> liveData.postValue(NetworkState.error(getErrorMessage(report)))
                report.needRefreshToken() -> liveData.postValue(NetworkState.REFRESH_TOKEN)
                else -> liveData.postValue(NetworkState.LOADED)
            }
        }
    })
    return liveData
}