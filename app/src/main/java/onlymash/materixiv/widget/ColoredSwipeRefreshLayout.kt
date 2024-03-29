package onlymash.materixiv.widget

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import onlymash.materixiv.R

class ColoredSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {

    init {
        setColorSchemeResources(
            R.color.blue,
            R.color.purple,
            R.color.green,
            R.color.orange,
            R.color.red
        )
    }
}