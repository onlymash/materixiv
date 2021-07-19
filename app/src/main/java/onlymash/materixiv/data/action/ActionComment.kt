package onlymash.materixiv.data.action

import okhttp3.HttpUrl
import onlymash.materixiv.app.Values

data class ActionComment(
    var auth: String,
    //illust or novel id
    var id: Long,
    var type: Int
) {

    val url: HttpUrl
        get() {
            return if (type == TYPE_ILLUST) {
                illustUrl
            } else {
                novelUrl
            }
        }

    private val illustUrl: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addPathSegments("v1/illust/comments")
            .addQueryParameter("illust_id", id.toString())
            .build()

    private val novelUrl: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addPathSegments("v3/novel/comments")
            .addQueryParameter("novel_id", id.toString())
            .build()

    private val novelRepliesUrl: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addPathSegments("v2/novel/comment/replies")
            .addQueryParameter("comment_id", id.toString())
            .build()

    companion object {
        const val TYPE_ILLUST = 0
        const val TYPE_NOVEL = 1
    }
}