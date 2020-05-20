package onlymash.materixiv.data.action

import okhttp3.HttpUrl
import onlymash.materixiv.app.Values

data class ActionNovel(
    var type: Int = Values.PAGE_TYPE_FOLLOWING,
    var auth: String = "",
    var myUserId: String = "",
    var restrict: Restrict = Restrict.PUBLIC,
    var word: String = "",
    //ranking
    var modeRanking: RankingMode = RankingMode.DAY,
    var date: String? = null,
    //bookmarks
    var destUserId: String = "-1"
) {
    val url: HttpUrl
        get() = when (type) {
            Values.PAGE_TYPE_RECOMMENDED -> recommendedUrl
            Values.PAGE_TYPE_FOLLOWING -> followingUrl
            Values.PAGE_TYPE_RANKING -> rankingUrl
            Values.PAGE_TYPE_BOOKMARKS -> bookmarksUrl
            Values.PAGE_TYPE_USER -> userUrl
            else -> searchUrl
        }

    private val baseUrlBuilder
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addQueryParameter("filter", "for_android")

    private val recommendedUrl: HttpUrl
        get() = baseUrlBuilder.addPathSegments("v1/novel/recommended").build()

    private val followingUrl: HttpUrl
        get() = baseUrlBuilder
            .addPathSegments("v1/novel/follow")
            .addQueryParameter("user_id", myUserId)
            .addQueryParameter("restrict", restrict.value)
            .build()

    private val rankingUrl: HttpUrl
        get() {
            val builder = baseUrlBuilder
                .addPathSegments("v1/novel/ranking")
                .addQueryParameter("mode", modeRanking.value)
            if (date != null) {
                builder.addQueryParameter("date", date)
            }
            return builder.build()
        }

    private val searchUrl: HttpUrl
        get() = baseUrlBuilder
            .addPathSegments("v1/search/novel")
            .addQueryParameter("word", word)
            .build()

    private val bookmarksUrl: HttpUrl
        get() = baseUrlBuilder
            .addPathSegments("v1/user/bookmarks/novel")
            .addQueryParameter("restrict", restrict.value)
            .addQueryParameter("user_id", destUserId)
            .build()

    private val userUrl: HttpUrl
        get() = baseUrlBuilder
            .addPathSegments("v1/user/novels")
            .addQueryParameter("user_id", destUserId)
            .build()
}