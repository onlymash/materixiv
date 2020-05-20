package onlymash.materixiv.extensions

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

fun Closeable.safeCloseQuietly() {
    try {
        close()
    } catch (_: IOException) {
        // Ignore
    }

}

fun InputStream?.copy(outputStream: OutputStream?): Long {
    if (this == null || outputStream == null) {
        return 0
    }
    return copyTo(outputStream)
}