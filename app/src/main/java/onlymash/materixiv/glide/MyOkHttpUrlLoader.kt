package onlymash.materixiv.glide

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.model.ModelLoader
import okhttp3.OkHttpClient
import onlymash.materixiv.app.Keys
import onlymash.materixiv.utils.HttpHeaders
import java.io.InputStream

class MyOkHttpUrlLoader(client: OkHttpClient) : OkHttpUrlLoader(client) {

    override fun buildLoadData(
        model: GlideUrl,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        val glideUrl = GlideUrl(model.toURL(), getHeaders())
        return super.buildLoadData(glideUrl, width, height, options)
    }

    private fun getHeaders(): Headers {
        return LazyHeaders.Builder()
            .addHeader(Keys.USER_AGENT, HttpHeaders.appUserAgent)
            .addHeader(Keys.REFERER, HttpHeaders.referer)
            .build()
    }
}