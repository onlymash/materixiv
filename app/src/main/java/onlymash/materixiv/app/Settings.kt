package onlymash.materixiv.app

import android.content.SharedPreferences
import androidx.core.content.edit
import okhttp3.dnsoverhttps.DnsOverHttps
import onlymash.materixiv.network.DoHProviders
import org.kodein.di.instance

inline fun <reified T> SharedPreferences?.getValue(key: String, default: T): T {
    if (this == null) {
        return default
    }
    @Suppress("UNCHECKED_CAST")
    return when (default) {
        is String -> getString(key, default) as T
        is Int -> getInt(key, default) as T
        is Long -> getLong(key, default) as T
        is Boolean -> getBoolean(key, default) as T
        is Float -> getFloat(key, default) as T
        is Set<*> -> getStringSet(key, default as Set<String>) as T
        else -> throw IllegalArgumentException("Generic type not handled: ${T::class.java.name}")
    }
}

inline fun <reified T> SharedPreferences.setValue(key: String, value: T) {
    edit {
        @Suppress("UNCHECKED_CAST")
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            is Set<*> -> putStringSet(key, value as Set<String>)
            else -> throw IllegalArgumentException("Generic type not handled: ${T::class.java.name}")
        }
    }
}

object Settings {
    private val sp by App.app.instance<SharedPreferences>()

    val dohEnabled get() = sp.getValue(Keys.NETWORK_DOH, true)

    private val dohProviderString get() = sp.getValue(Keys.NETWORK_DOH_PROVIDER, "cloudflare")

    val dohProvider: DnsOverHttps
        get() {
            return when (dohProviderString) {
                "google" -> DoHProviders.googleDns
                else -> DoHProviders.cloudflareDns
            }
        }

    val fuckGFW get() = sp.getValue(Keys.NETWORK_FUCK_GFW, true)
}