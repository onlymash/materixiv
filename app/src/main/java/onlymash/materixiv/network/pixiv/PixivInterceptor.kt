package onlymash.materixiv.network.pixiv

import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.extensions.md5
import onlymash.materixiv.utils.HttpHeaders
import java.text.SimpleDateFormat
import java.util.*

class PixivInterceptor : Interceptor {
    companion object {
        fun getAppAcceptLanguage(locale: Locale): String {
            return when (val language = locale.language) {
                "ja", "ko" -> language
                "zh" -> if (locale.country == "CN") "zh-hans" else "zh-hant"
                else -> "en"
            }
        }
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val dateFormat = SimpleDateFormat(Values.DATE_FORMAT_NSISO8601, Locale.getDefault())
        val date = dateFormat.format(Date())
        val locale = Locale.getDefault()
        val builder =  chain.request().newBuilder()
            .removeHeader(Keys.USER_AGENT)
            .addHeader(Keys.USER_AGENT, HttpHeaders.appUserAgent)
            .addHeader(Keys.APP_OS, "android")
            .addHeader(Keys.APP_OS_VERSION, Build.VERSION.RELEASE)
            .addHeader(Keys.APP_VERSION, Values.APP_VERSION)
            .addHeader(Keys.APP_ACCEPT_LANGUAGE, getAppAcceptLanguage(locale))
            .addHeader(Keys.ACCEPT_LANGUAGE, locale.toString())
            .addHeader(Keys.X_CLIENT_HASH, "$date${Values.APP_HASH_SALT}".md5())
            .addHeader(Keys.X_CLIENT_TIME, date)
        return chain.proceed(builder.build())
    }
}

class PixivDownloadInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder =  chain.request().newBuilder()
            .removeHeader(Keys.USER_AGENT)
            .addHeader(Keys.USER_AGENT, HttpHeaders.appUserAgent)
            .addHeader(Keys.REFERER, HttpHeaders.referer)
        return chain.proceed(builder.build())
    }
}