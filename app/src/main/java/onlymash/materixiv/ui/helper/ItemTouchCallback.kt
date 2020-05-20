package onlymash.materixiv.ui.helper

interface ItemTouchCallback {

    fun onSwipeItem(position: Int)

    fun onDragItem(position: Int, targetPosition: Int)

    val isSwipeEnabled: Boolean

    val isDragEnabled: Boolean
}