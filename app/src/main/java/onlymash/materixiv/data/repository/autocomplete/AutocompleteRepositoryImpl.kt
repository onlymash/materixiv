package onlymash.materixiv.data.repository.autocomplete

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.common.Tag

class AutocompleteRepositoryImpl(private val api: PixivAppApi) : AutocompleteRepository {

    override suspend fun getTags(auth: String, word: String?): List<Tag>? {
        return withContext(Dispatchers.IO) {
            try {
                api.autocomplete(auth, word).body()?.tags
            } catch (_: Exception) {
                null
            }
        }
    }

    override suspend fun getUsers(auth: String, word: String?): List<String>? {
        if (word == null) {
            return null
        }
        return withContext(Dispatchers.IO) {
            try {
                api.getUsers(auth, getSearchUserUrl(word)).body()?.userPreviews?.map { it.user.name }
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun getSearchUserUrl(word: String): HttpUrl {
        return HttpUrl.Builder()
            .scheme("https")
            .host(Values.HOST_APP)
            .addPathSegments("v1/search/user")
            .addQueryParameter("filter", "for_android")
            .addQueryParameter("word", word)
            .build()
    }
}