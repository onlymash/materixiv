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

    override suspend fun getToken(code: String, codeVerifier: String): NetResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val time = System.currentTimeMillis()
                val response = api.getToken(code = code, codeVerifier = codeVerifier)
                val token = Token(
                    time = time,
                    userId = response.detail.user.id,
                    data = response.detail
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

    override suspend fun refresh(uid: Long, refreshToken: String): NetResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val time = System.currentTimeMillis()
                val response = api.refreshToken(refreshToken = refreshToken)
                val token = Token(
                    uid = uid,
                    time = time,
                    userId = response.detail.user.id,
                    data = response.detail
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