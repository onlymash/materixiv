package onlymash.materixiv.extensions

/**
 * A generic class that holds a value with its net response status.
 * @param <T>
 */

sealed class NetResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetResult<T>()
    data class Error(val e: Exception): NetResult<Nothing>()
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=${e.message}]"
        }
    }
}