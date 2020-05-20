package onlymash.materixiv.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.renderscript.*
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import java.security.MessageDigest

class BlurTransformation(
    private val radius: Int = MAX_RADIUS,
    private val sampling: Int = DEFAULT_DOWN_SAMPLING
) : BitmapTransformation() {

    override fun transform(
        context: Context,
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val width = toTransform.width
        val height = toTransform.height
        val scaledWidth = width / sampling
        val scaledHeight = height / sampling
        var bitmap = pool[scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888]
        setCanvasBitmapDensity(toTransform, bitmap)
        val canvas = Canvas(bitmap)
        canvas.scale(1 / sampling.toFloat(), 1 / sampling.toFloat())
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(toTransform, 0f, 0f, paint)
        try {
            bitmap = blur(context, bitmap, radius)
        } catch (_: RSRuntimeException) {

        }
        return bitmap
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((this::class.java.name + radius + sampling).toByteArray(Key.CHARSET))
    }

    companion object {
        private const val MAX_RADIUS = 25
        private const val DEFAULT_DOWN_SAMPLING = 1

        @Throws(RSRuntimeException::class)
        private fun blur(context: Context?, bitmap: Bitmap, radius: Int): Bitmap {
            var rs: RenderScript? = null
            var input: Allocation? = null
            var output: Allocation? = null
            var blur: ScriptIntrinsicBlur? = null
            try {
                rs = RenderScript.create(context)
                rs.messageHandler = RenderScript.RSMessageHandler()
                input = Allocation.createFromBitmap(
                    rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT
                )
                output = Allocation.createTyped(rs, input.type)
                blur = ScriptIntrinsicBlur.create(
                    rs,
                    Element.U8_4(rs)
                )
                blur.setInput(input)
                blur.setRadius(radius.toFloat())
                blur.forEach(output)
                output.copyTo(bitmap)
            } finally {
                if (rs != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        RenderScript.releaseAllContexts()
                    } else {
                        rs.destroy()
                    }
                }
                input?.destroy()
                output?.destroy()
                blur?.destroy()
            }
            return bitmap
        }
    }
}