package onlymash.materixiv.widget

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(@Px spaceSize: Int) : RecyclerView.ItemDecoration() {

    @Px
    private val halfSpaceSize = spaceSize / 2

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State) {

        if (parent.paddingLeft != halfSpaceSize) {
            parent.updatePadding(halfSpaceSize)
            parent.clipToPadding = false
        }
        outRect.set(halfSpaceSize, halfSpaceSize, halfSpaceSize, halfSpaceSize)
    }
}