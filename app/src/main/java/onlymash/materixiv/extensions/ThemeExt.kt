package onlymash.materixiv.extensions

import android.content.res.Configuration

fun Configuration.isNightTheme() =
    uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES