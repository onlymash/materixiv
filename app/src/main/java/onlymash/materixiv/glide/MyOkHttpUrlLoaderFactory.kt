package onlymash.materixiv.glide

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.OkHttpClient
import java.io.InputStream

class MyOkHttpUrlLoaderFactory(
    private val client: OkHttpClient
) : OkHttpUrlLoader.Factory(client) {
    override fun build(
        multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
        return MyOkHttpUrlLoader(client)
    }
}