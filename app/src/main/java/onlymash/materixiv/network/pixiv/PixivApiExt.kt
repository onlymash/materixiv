package onlymash.materixiv.network.pixiv

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
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

inline fun <reified T> createApi(): T {
    val contentType = "application/json".toMediaType()
    val jsonConfiguration = JsonConfiguration(
        ignoreUnknownKeys = true,
        isLenient = true
    )
    val baseUrl = when (T::class.java) {
        PixivOauthApi::class.java -> Values.BASE_URL_OAUTH
        PixivAppApi::class.java -> Values.BASE_URL_APP
        else -> Values.BASE_URL
    }
    val factory = Json(jsonConfiguration).asConverterFactory(contentType)
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(pixivClient)
        .addConverterFactory(factory)
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
    get() = HttpLoggingInterceptor(
        object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("PixivApi", message)
            }
        }
    ).apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }