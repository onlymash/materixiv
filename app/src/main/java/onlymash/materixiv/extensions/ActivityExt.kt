package onlymash.materixiv.extensions

import android.app.Activity
import android.util.DisplayMetrics

fun Activity.getWindowWidth(): Int {
    val outMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.widthPixels
}

fun Activity.getWindowHeight(): Int {
    val outMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.heightPixels
}