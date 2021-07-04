package onlymash.materixiv.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import onlymash.materixiv.R

private fun getCustomTabsIntent(context: Context): CustomTabsIntent {
    return CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.colorBackground))
            .build())
        .build()
}

fun Context.launchUrl(uri: Uri) = try {
    getCustomTabsIntent(this).launchUrl(this, uri)
} catch (e: ActivityNotFoundException) { e.printStackTrace() }

fun Context.launchUrl(url: String) = this.launchUrl(Uri.parse(url))