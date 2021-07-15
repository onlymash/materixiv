package onlymash.materixiv.data.repository.token

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import onlymash.materixiv.data.api.PixivOauthApi
import onlymash.materixiv.data.db.dao.TokenDao
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.extensions.NetResult

class TokenRepositoryImpl(private val api: PixivOauthApi,
                          private val dao: TokenDao) : TokenRepository {

    override fun getAllTokens(): Flow<List<Token>> {
        return dao.getAllTokensFlow().distinctUntilChanged()
    }

    override suspend fun getToken(code: String, codeVerifier: String): NetResult<Token> {
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
                NetResult.Success(token)
            } catch (e: Exception) {
                NetResult.Error(e)
            }
        }
    }

    override suspend fun refresh(uid: Long, refreshToken: String): NetResult<Token> {
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
                NetResult.Success(token)
            } catch (e: Exception) {
                NetResult.Error(e)
            }
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dao.deleteAll()
        }
    }
}