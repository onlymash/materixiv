package onlymash.materixiv.app

object Values {
    const val APP_DB_NAME = "materixiv.db"

    const val APP_VERSION = "6.15.0"
    const val APP_HASH_SALT = "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c"

    const val HOST_WEB = "www.pixiv.net"
    const val HOST_APP = "app-api.pixiv.net"
    const val BASE_URL = "https://www.pixiv.net"
    const val BASE_URL_APP = "https://app-api.pixiv.net"
    const val BASE_URL_ACCOUNT = "https://accounts.pixiv.net"
    const val BASE_URL_OAUTH = "https://oauth.secure.pixiv.net"
    const val REDIRECT_URL = "https://app-api.pixiv.net/web/v1/users/auth/pixiv/callback"

    const val CLIENT_ID = "MOBrBDS8blbauoSck0ZfDbtuzpyT"
    const val CLIENT_SECRET= "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj"
    const val GRANT_TYPE_PASSWORD = "password"
    const val GRANT_TYPE_REFRESH_TOKEN = "refresh_token"
    const val GRANT_TYPE_AUTH_CODE = "authorization_code"

    const val DATE_FORMAT_NSISO8601 = "yyyy-MM-dd'T'HH:mm:ssZZZZZ"

    const val PAGE_TYPE_FOLLOWING = 0
    const val PAGE_TYPE_RECOMMENDED = 1
    const val PAGE_TYPE_RANKING = 2
    const val PAGE_TYPE_SEARCH = 3
    const val PAGE_TYPE_BOOKMARKS = 4
    const val PAGE_TYPE_USER = 5
    const val PAGE_TYPE_FRIENDS = 6
    const val PAGE_TYPE_FOLLOWER = 7
    const val PAGE_TYPE_RELATED = 8

    const val SEARCH_TYPE_ILLUST = 0
    const val SEARCH_TYPE_NOVEL = 1
    const val SEARCH_TYPE_USER = 2

    const val PREFERENCE_NAME_SETTINGS = "settings"
}