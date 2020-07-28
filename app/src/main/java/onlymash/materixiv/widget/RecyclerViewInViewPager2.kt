package onlymash.materixiv.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class RecyclerViewInViewPager2 @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var startX = 0f
    private var startY = 0f

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val disX = abs(ev.x - startX)
                val disY = abs(ev.y - startY)
                val angle = atan2(disY, disX) * 180 / PI
                parent.requestDisallowInterceptTouchEvent(angle > 20)
            }
            MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.dispatchTouchEvent(ev)
    }
}