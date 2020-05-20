package onlymash.materixiv.data.action

import okhttp3.HttpUrl
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.db.entity.Token

data class ActionIllust(
    var type: Int = Values.PAGE_TYPE_FOLLOWING,
    var token: Token,

    //follow
    var restrict: Restrict = Restrict.PUBLIC,

    //ranking
    var modeRanking: RankingMode = RankingMode.DAY,
    var date: String? = null,

    //search
    var query: String = "",
    var sort: Sort = Sort.DATE_DESC,
    var startDate: String? = null,
    var endDate: String? = null,
    var bookmarkNum: Int? = null,
    var searchTarget: SearchTarget = SearchTarget.PARTIAL_MATCH,
    var duration: Duration? = null,
    //bookmarks
    var destUserId: String = "-1",
    // related
    var illustId: Long = 0
) {
    val dbQuery: String
        get() {
            return when (type) {
                Values.PAGE_TYPE_FOLLOWING -> "$type:following"
                Values.PAGE_TYPE_RECOMMENDED -> "$type:recommended"
                Values.PAGE_TYPE_RANKING -> "$type:ranking"
                Values.PAGE_TYPE_BOOKMARKS -> "$type:bookmarks:$destUserId"
                Values.PAGE_TYPE_USER -> "$type:user:$destUserId"
                Values.PAGE_TYPE_RELATED -> "$type:related:$illustId"
                else -> "$type:search:$query"
            }
        }

    fun getUrl(offset: Int): HttpUrl {
        val builder = when (type) {
            Values.PAGE_TYPE_FOLLOWING -> followUrlBuilder
            Values.PAGE_TYPE_RANKING -> rankingUrlBuilder
            Values.PAGE_TYPE_RECOMMENDED -> recommendedUrlBuilder
            Values.PAGE_TYPE_BOOKMARKS -> bookmarksUrlBuilder
            Values.PAGE_TYPE_USER -> userUrlBuilder
            Values.PAGE_TYPE_RELATED -> relatedUrlBuilder
            else -> searchUrlBuilder
        }
        if (offset > 0) {
            builder.addQueryParameter("offset", offset.toString())
        }
        return builder.build()
    }

    private val baseUrlBuilder
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addQueryParameter("filter", "for_android")

    private val rankingUrlBuilder: HttpUrl.Builder
        get() {
            val builder = baseUrlBuilder
                .addPathSegments("v1/illust/ranking")
                .addQueryParameter("mode", modeRanking.value)
            if (date != null) {
                builder.addQueryParameter("date", date)
            }
            return builder
        }

    private val recommendedUrlBuilder
        get() = baseUrlBuilder
            .addPathSegments("v1/illust/recommended")
            .addQueryParameter("include_ranking_label", "true")
            .addQueryParameter("include_ranking_illusts", "false")
            .addQueryParameter("include_privacy_policy", "false")

    private val followUrlBuilder
        get() = baseUrlBuilder
            .addPathSegments("v2/illust/follow")
            .addQueryParameter("restrict", restrict.value)

    private val searchUrlBuilder: HttpUrl.Builder
        get() {
            val builder = if (!token.data.profile.isPremium && sort == Sort.POPULAR_DESC) {
                baseUrlBuilder.addPathSegments("v1/search/popular-preview/illust")
            } else {
                baseUrlBuilder.addPathSegments("v1/search/illust")
            }
                .addQueryParameter("word", query)
                .addQueryParameter("sort", sort.value)
                .addQueryParameter("search_target", searchTarget.value)
                .addQueryParameter("merge_plain_keyword_results", "true")
            if (startDate != null) {
                builder.addQueryParameter("start_date", startDate)
            }
            if (endDate != null) {
                builder.addQueryParameter("end_date", endDate)
            }
            if (bookmarkNum != null) {
                builder.addQueryParameter("bookmark_num", bookmarkNum.toString())
            }
            if (duration != null) {
                builder.addQueryParameter("duration", duration?.value)
            }
            return builder
        }

    private val bookmarksUrlBuilder: HttpUrl.Builder
        get() = baseUrlBuilder
            .addPathSegments("v1/user/bookmarks/illust")
            .addQueryParameter("restrict", restrict.value)
            .addQueryParameter("user_id", destUserId)

    private val userUrlBuilder: HttpUrl.Builder
        get() = baseUrlBuilder
            .addPathSegments("v1/user/illusts")
            .addQueryParameter("user_id", destUserId)

    private val relatedUrlBuilder: HttpUrl.Builder
        get() = baseUrlBuilder
            .addPathSegments("v2/illust/related")
            .addQueryParameter("illust_id", illustId.toString())
}