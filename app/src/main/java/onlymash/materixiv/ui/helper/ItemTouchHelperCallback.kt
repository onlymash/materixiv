package onlymash.materixiv.ui.helper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(
    private val itemTouchCallback: ItemTouchCallback) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        itemTouchCallback.onDragItem(
            position = viewHolder.absoluteAdapterPosition,
            targetPosition = target.absoluteAdapterPosition
        )
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemTouchCallback.onSwipeItem(viewHolder.absoluteAdapterPosition)
    }

    override fun isLongPressDragEnabled(): Boolean =
        itemTouchCallback.isDragEnabled

    override fun isItemViewSwipeEnabled(): Boolean =
        itemTouchCallback.isSwipeEnabled
}