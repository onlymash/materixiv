package onlymash.materixiv.data.api

import onlymash.materixiv.app.Values
import onlymash.materixiv.data.model.TokenResponse
import retrofit2.Response
import retrofit2.http.*

interface PixivOauthApi {

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @FieldMap map: Map<String, String> = mapOf(
            "client_id" to Values.CLIENT_ID,
            "client_secret" to Values.CLIENT_SECRET,
            "grant_type" to Values.GRANT_TYPE_PASSWORD,
            "get_secure_url" to "1",
            "include_policy" to "1",
            "device_token" to "pixiv"
        )
    ): Response<TokenResponse>

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("device_token") deviceToken: String,
        @FieldMap map: Map<String, String> = mapOf(
            "client_id" to Values.CLIENT_ID,
            "client_secret" to Values.CLIENT_SECRET,
            "grant_type" to Values.GRANT_TYPE_REFRESH_TOKEN,
            "get_secure_url" to "1",
            "include_policy" to "1"
        )
    ): Response<TokenResponse>
}