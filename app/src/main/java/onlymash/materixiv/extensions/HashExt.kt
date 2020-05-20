package onlymash.materixiv.extensions

import java.security.MessageDigest

/**
 * Supported algorithms on Android:
 *
 * Algorithm	Supported API Levels
 * MD5          1+
 * SHA-1	    1+
 * SHA-224	    1-8,22+
 * SHA-256	    1+
 * SHA-384	    1+
 * SHA-512	    1+
 */

private fun String.hashString(type: String): String {
    val hexChars = "0123456789abcdef"
    val bytes = MessageDigest.getInstance(type).digest(toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach { byte ->
        val i = byte.toInt()
        result.append(hexChars[i shr 4 and 0x0f])
        result.append(hexChars[i and 0x0f])
    }

    return result.toString()
}

fun String.sha512() = hashString("SHA-512")

fun String.sha256() = hashString("SHA-256")

fun String.sha1() = hashString("SHA-1")

fun String.md5() = hashString("MD5")