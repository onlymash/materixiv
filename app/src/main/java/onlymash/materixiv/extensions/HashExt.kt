package onlymash.materixiv.extensions

import java.security.MessageDigest


private fun String.hashString(type: String): String {
    val bytes = MessageDigest.getInstance(type).digest(toByteArray())
    return toHex(bytes)
}

private fun toHex(bytes: ByteArray): String {
    return with(StringBuilder()) {
        bytes.forEach { byte ->
            val hex = byte.toInt() and (0xFF)
            val hexStr = Integer.toHexString(hex)
            if (hexStr.length == 1) {
                append("0").append(hexStr)
            } else {
                append(hexStr)
            }
        }
        this.toString()
    }
}

fun String.sha512() = hashString("SHA-512")

fun String.sha256() = hashString("SHA-256")

fun String.sha1() = hashString("SHA-1")

fun String.md5() = hashString("MD5")