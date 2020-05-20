package onlymash.materixiv.extensions

import android.view.View
import android.view.Window
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding

fun View.toDrawBar() {
    systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
}

fun View.toFullscreenImmersive() {
    systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE
}


inline var Window.isFullscreen: Boolean
    get() = (decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_STABLE) != 0
    set(value) {
        if (value) {
            decorView.toFullscreenImmersive()
        } else {
            decorView.toDrawBar()
        }
    }

fun AppCompatActivity.setupInsets(insetsCallback: (insets: WindowInsets) -> Unit) {
    findViewById<View>(android.R.id.content).apply {
        toDrawBar()
        setOnApplyWindowInsetsListener { _, insets ->
            updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight
            )
            insetsCallback(insets)
            insets
        }
    }
}