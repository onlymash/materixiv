package onlymash.materixiv.network.pixiv

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import onlymash.materixiv.BuildConfig
import onlymash.materixiv.app.Settings
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.api.PixivOauthApi
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private val defaultJson
    get() = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

val converterFactory
    get() = defaultJson.asConverterFactory("application/json".toMediaType())

inline fun <reified T> createApi(): T {
    val baseUrl = when (T::class.java) {
        PixivOauthApi::class.java -> Values.BASE_URL_OAUTH
        PixivAppApi::class.java -> Values.BASE_URL_APP
        else -> Values.BASE_URL
    }
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(pixivClient)
        .addConverterFactory(converterFactory)
        .build()
        .create(T::class.java)
}

val pixivClient: OkHttpClient
    get() {
        val builder = pixivClientBuilder
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(PixivInterceptor())
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

val pixivClientBuilder: OkHttpClient.Builder
    get() {
        val builder = OkHttpClient.Builder()
        if (Settings.dohEnabled) {
            builder.dns(Settings.dohProvider)
        }
        if (Settings.fuckGFW) {
            builder.sslSocketFactory(PixivSSLSocketFactory(), PixivX509TrustManager())
        }
        return builder
    }

private val loggingInterceptor: HttpLoggingInterceptor
    get() = HttpLoggingInterceptor { message -> Log.d("PixivApi", message) }.apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }