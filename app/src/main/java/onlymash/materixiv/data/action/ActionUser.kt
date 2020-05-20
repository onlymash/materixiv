package onlymash.materixiv.data.action

import okhttp3.HttpUrl
import onlymash.materixiv.app.Values

data class ActionUser(
    var type: Int = Values.PAGE_TYPE_FOLLOWING,
    var auth: String = "",
    var userId: String = "",
    var restrict: Restrict = Restrict.PUBLIC,
    var word: String = ""
) {
    val url: HttpUrl
        get() = when (type) {
            Values.PAGE_TYPE_RECOMMENDED -> recommendedUrl
            Values.PAGE_TYPE_FOLLOWING -> followingUrl
            Values.PAGE_TYPE_FOLLOWER -> followerUrl
            Values.PAGE_TYPE_FRIENDS -> friendsUrl
            else -> searchUrl
        }

    private val baseUrlBuilder
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addQueryParameter("filter", "for_android")

    private val recommendedUrl: HttpUrl
        get() = baseUrlBuilder.addPathSegments("v1/user/recommended").build()

    private val followingUrl: HttpUrl
        get() = baseUrlBuilder
            .addPathSegments("v1/user/following")
            .addQueryParameter("user_id", userId)
            .addQueryParameter("restrict", restrict.value)
            .build()

    private val followerUrl: HttpUrl
        get() = baseUrlBuilder
            .addPathSegments("v1/user/follower")
            .addQueryParameter("user_id", userId)
            .addQueryParameter("restrict", restrict.value)
            .build()

    private val friendsUrl: HttpUrl
        get() = baseUrlBuilder
            .addPathSegments("v1/user/mypixiv")
            .addQueryParameter("user_id", userId)
            .addQueryParameter("restrict", restrict.value)
            .build()

    private val searchUrl: HttpUrl
        get() = baseUrlBuilder
            .addPathSegments("v1/search/user")
            .addQueryParameter("word", word)
            .build()
}