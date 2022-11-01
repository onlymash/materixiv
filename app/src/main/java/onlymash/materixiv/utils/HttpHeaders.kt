package onlymash.materixiv.utils

import android.os.Build
import androidx.core.os.LocaleListCompat
import onlymash.materixiv.app.Values
import java.util.*

object HttpHeaders {

    val referer: String
        get() = Values.BASE_URL_APP

    val appUserAgent: String
        get() = "PixivAndroidApp/${Values.APP_VERSION} (Android ${Build.VERSION.RELEASE}; ${Build.MODEL})"

    val acceptedLanguageHeader: String
        get() = getAcceptedLanguageHeaderValue()

    private fun getAcceptedLanguageHeaderValue(): String {
        var weight = 1.0F
        return getPreferredLocaleList()
            .map { it.toLanguageTag() }
            .reduce { accumulator, languageTag ->
                weight -= 0.1F
                "$accumulator,$languageTag;q=$weight"
            }
    }

    private fun getPreferredLocaleList(): List<Locale> {
        val adjustedLocaleListCompat = LocaleListCompat.getAdjustedDefault()
        val preferredLocaleList = mutableListOf<Locale>()
        for (index in 0 until adjustedLocaleListCompat.size()) {
            adjustedLocaleListCompat.get(index)?.let {
                preferredLocaleList.add(it)
            }
        }
        return preferredLocaleList
    }
}