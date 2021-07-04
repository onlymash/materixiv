package onlymash.materixiv.extensions

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets


fun Activity.getWindowWidth(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val outMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }
}

fun Activity.getWindowHeight(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.height() - insets.top - insets.bottom
    } else {
        val outMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }
}