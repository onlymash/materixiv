package onlymash.materixiv.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import onlymash.materixiv.network.ProgressInterceptor
import onlymash.materixiv.network.pixiv.pixivClientBuilder
import java.io.InputStream

@GlideModule
class MyGildeModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val requestOptions = RequestOptions
            .formatOf(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        builder.setDefaultRequestOptions(requestOptions)
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = pixivClientBuilder
            .addInterceptor(ProgressInterceptor())
            .build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, MyOkHttpUrlLoaderFactory(client))
    }

    override fun isManifestParsingEnabled(): Boolean = false
}