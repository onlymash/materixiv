package onlymash.materixiv.extensions

/**
 * A generic class that holds a value with its net response status.
 * @param <T>
 */

sealed class NetResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetResult<T>()
    data class HttpCode(val code : Int) : NetResult<Nothing>()
    data class Error(val errorMsg: String): NetResult<Nothing>()
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is HttpCode -> "HttpCode[code=$code]"
            is Error -> "Error[exception=$errorMsg]"
        }
    }
}