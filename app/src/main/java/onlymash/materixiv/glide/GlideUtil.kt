package onlymash.materixiv.glide

import com.bumptech.glide.request.target.Target

object GlideUtil {

    fun isValidDimensions(width: Int, height: Int): Boolean {
        return isValidDimension(width) && isValidDimension(
            height
        )
    }

    private fun isValidDimension(dimen: Int): Boolean {
        return dimen > 0 || dimen == Target.SIZE_ORIGINAL
    }
}