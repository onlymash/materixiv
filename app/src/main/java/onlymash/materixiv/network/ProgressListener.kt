package onlymash.materixiv.network

interface ProgressListener {
    fun onUpdate(
        bytesRead: Long,
        contentLength: Long,
        done: Boolean
    )
}