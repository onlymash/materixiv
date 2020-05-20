package onlymash.materixiv.data.repository

data class NetworkState(
    val status: Status,
    val msg: String? = null
) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        val REFRESH_TOKEN = NetworkState(Status.REFRESH_TOKEN)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}

fun NetworkState?.isRunning() = this?.status == Status.RUNNING

fun NetworkState?.isSuccess() = this?.status == Status.SUCCESS

fun NetworkState?.isRefreshToken() = this?.status == Status.REFRESH_TOKEN

fun NetworkState?.isFailed() = this?.status == Status.FAILED

fun NetworkState?.hasMsg() = this?.msg != null