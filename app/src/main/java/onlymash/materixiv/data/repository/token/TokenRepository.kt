package onlymash.materixiv.data.repository.token

import androidx.lifecycle.LiveData
import onlymash.materixiv.data.db.entity.Token
import onlymash.materixiv.extensions.NetResult

interface TokenRepository {

    suspend fun load(): LiveData<List<Token>>

    suspend fun getToken(
        code: String,
        codeVerifier: String,
    ): NetResult<Boolean>

    suspend fun refresh(
        uid: Long,
        refreshToken: String
    ): NetResult<Boolean>
}