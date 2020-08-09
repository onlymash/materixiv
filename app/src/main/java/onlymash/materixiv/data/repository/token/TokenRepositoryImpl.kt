package onlymash.materixiv.data.repository.token

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.api.PixivOauthApi
import onlymash.materixiv.data.db.dao.TokenDao
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.extensions.NetResult
import retrofit2.HttpException

class TokenRepositoryImpl(private val api: PixivOauthApi,
                          private val dao: TokenDao) : TokenRepository {

    override suspend fun load(): LiveData<List<Token>> {
        return withContext(Dispatchers.IO) {
            dao.getAllTokensLiveData()
        }
    }

    override suspend fun login(username: String, password: String): NetResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val time = System.currentTimeMillis()
                val response = api.login(username = username, password = password)
                val token = Token(
                    time = time,
                    userId = response.data.profile.id,
                    data = response.data
                )
                dao.insert(token)
                NetResult.Success(true)
            } catch (e: Exception) {
                if (e is HttpException) {
                    NetResult.HttpCode(e.code())
                } else {
                    NetResult.Error(e.message.toString())
                }
            }
        }
    }

    override suspend fun refresh(uid: Long, refreshToken: String, deviceToken: String): NetResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val time = System.currentTimeMillis()
                val response = api.refreshToken(refreshToken, deviceToken)
                val token = Token(
                    uid = uid,
                    time = time,
                    userId = response.data.profile.id,
                    data = response.data
                )
                dao.update(token)
                NetResult.Success(true)
            } catch (e: Exception) {
                if (e is HttpException) {
                    NetResult.HttpCode(e.code())
                } else {
                    NetResult.Error(e.message.toString())
                }
            }
        }
    }
}