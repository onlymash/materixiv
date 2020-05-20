package onlymash.materixiv.glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.request.target.Target

abstract class BitmapTransformation : Transformation<Bitmap> {
    override fun transform(
        context: Context,
        resource: Resource<Bitmap>,
        outWidth: Int,
        outHeight: Int
    ): Resource<Bitmap> {
        if (!GlideUtil.isValidDimensions(outWidth, outHeight)) {
            throw IllegalArgumentException("Cannot apply transformation on width: " + outWidth +
                    " or height: " + outHeight + " less than or equal to zero and not Target.SIZE_ORIGINAL")
        }
        val bitmapPool = GlideApp.get(context).bitmapPool
        val toTransform = resource.get()
        val targetWidth = if (outWidth == Target.SIZE_ORIGINAL) toTransform.width else outWidth
        val targetHeight = if (outHeight == Target.SIZE_ORIGINAL) toTransform.height else outHeight
        val transformed = transform(
            context.applicationContext,
            bitmapPool,
            toTransform,
            targetWidth,
            targetHeight
        )
        return if (toTransform == transformed) {
            resource
        } else {
            BitmapResource(transformed, bitmapPool)
        }
    }

    protected abstract fun transform(
        context: Context,
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap

    open fun setCanvasBitmapDensity(toTransform: Bitmap, canvasBitmap: Bitmap) {
        canvasBitmap.density = toTransform.density
    }
}