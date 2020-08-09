package onlymash.materixiv.data.action

import okhttp3.HttpUrl
import onlymash.materixiv.app.Values

data class ActionComment(
    val auth: String,
    val illustId: Long
) {
    val url: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addPathSegments("v1/illust/comments")
            .addQueryParameter("illust_id", illustId.toString())
            .build()
}