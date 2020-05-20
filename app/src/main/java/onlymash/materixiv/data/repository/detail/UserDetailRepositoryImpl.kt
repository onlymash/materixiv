package onlymash.materixiv.data.repository.detail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.api.PixivAppApi
import onlymash.materixiv.data.model.UserDetailResponse

class UserDetailRepositoryImpl(private val pixivAppApi: PixivAppApi) : UserDetailRepository {

    override suspend fun getUserDetail(auth: String, userId: String): UserDetailResponse? {
        return withContext(Dispatchers.IO) {
            try {
                pixivAppApi.getUserDetail(auth, userId).body()
            } catch (_: Exception) {
                null
            }
        }
    }
}