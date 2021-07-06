package onlymash.materixiv.data.repository.token

import kotlinx.coroutines.flow.Flow
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.extensions.NetResult

interface TokenRepository {

    fun getAllTokens(): Flow<List<Token>>

    suspend fun getToken(
        code: String,
        codeVerifier: String,
    ): NetResult<Token>

    suspend fun refresh(
        uid: Long,
        refreshToken: String
    ): NetResult<Token>
}