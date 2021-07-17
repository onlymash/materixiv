package onlymash.materixiv.data.action

import androidx.core.util.Pair
import okhttp3.HttpUrl
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.ui.module.common.SharedViewModel

data class ActionNovel(
    var type: Int = Values.PAGE_TYPE_FOLLOWING,
    var token: Token,
    //follow
    var restrict: Restrict = Restrict.PUBLIC,
    //search
    var word: String = "",
    var sort: Sort = Sort.DATE_DESC,
    var dateRange: Pair<String, String>? = null,
    var searchTarget: SearchTarget = SearchTarget.PARTIAL_MATCH,
    var duration: Duration = Duration.ALL,
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
            .addQueryParameter("include_translated_tag_results", "true")
            .addQueryParameter("merge_plain_keyword_results", "true")

    private val recommendedUrl: HttpUrl
        get() = baseUrlBuilder.addPathSegments("v1/novel/recommended").build()

    private val followingUrl: HttpUrl
        get() = baseUrlBuilder
            .addPathSegments("v1/novel/follow")
            .addQueryParameter("user_id", token.userId)
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
        get() {
            val builder = if (!token.data.user.isPremium && sort == Sort.POPULAR_DESC) {
                baseUrlBuilder.addPathSegments("v1/search/popular-preview/novel")
            } else {
                baseUrlBuilder.addPathSegments("v1/search/novel")
            }
            builder.addQueryParameter("word", word)
                .addQueryParameter("sort", sort.value)
                .addQueryParameter("search_target", searchTarget.value)
            val range = when (duration) {
                Duration.CUSTOM -> dateRange
                Duration.LAST_DAY -> SharedViewModel.lastDayRange
                Duration.LAST_WEEK -> SharedViewModel.lastWeekRange
                Duration.LAST_MONTH -> SharedViewModel.lastMonthRange
                Duration.HALF_YEAR -> SharedViewModel.lastHalfYearRange
                Duration.YEAR -> SharedViewModel.lastYearRange
                Duration.ALL -> null
            }
            if (range != null) {
                builder.addQueryParameter("start_date", range.first)
                    .addQueryParameter("end_date", range.second)
            }
            return builder.build()
        }

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