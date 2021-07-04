package onlymash.materixiv.ui.module.login

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.security.MessageDigest

class LoginViewModel : ViewModel() {

    private val _codeVerifier = MutableLiveData(generateRandomString(32))

    val codeVerifier: String
        get() {
            var cv = _codeVerifier.value
            if (cv == null) {
                cv = generateRandomString(32)
                _codeVerifier.value = cv
            }
            return cv
        }

    val codeChallenge get() = generateCodeChallenge(codeVerifier)

    val code = MutableLiveData<String>()

    fun updateCode(newCode: String) {
        if (code.value != newCode) {
            code.value = newCode
        }
    }

    companion object {
        fun generateRandomString(length: Int) : String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }
        fun generateCodeChallenge(codeVerifier: String): String {
            val bytes = codeVerifier.toByteArray(Charsets.US_ASCII)
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(bytes, 0, bytes.size)
            val digest = messageDigest.digest()
            return Base64.encodeToString(
                digest,
                Base64.NO_PADDING or Base64.URL_SAFE or Base64.NO_WRAP
            )
        }
    }
}