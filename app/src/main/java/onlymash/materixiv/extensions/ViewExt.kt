package onlymash.materixiv.extensions

import android.widget.ImageView
import androidx.appcompat.widget.TooltipCompat


fun ImageView.setupTooltipText() {
    TooltipCompat.setTooltipText(this, contentDescription)
}