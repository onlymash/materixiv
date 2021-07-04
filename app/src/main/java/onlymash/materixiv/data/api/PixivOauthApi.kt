package onlymash.materixiv.data.api

import onlymash.materixiv.app.Values
import onlymash.materixiv.data.model.TokenResponse
import retrofit2.http.*

interface PixivOauthApi {

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun getToken(
        @Field("code") code: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("client_id") clientId: String = Values.CLIENT_ID,
        @Field("client_secret") clientSecret: String = Values.CLIENT_SECRET,
        @Field("grant_type") grantType: String = Values.GRANT_TYPE_AUTH_CODE,
        @Field("include_policy") includePolicy: Boolean = true,
        @Field("redirect_uri") redirectUri: String = Values.REDIRECT_URL
    ): TokenResponse

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String = Values.CLIENT_ID,
        @Field("client_secret") clientSecret: String = Values.CLIENT_SECRET,
        @Field("grant_type") grantType: String = Values.GRANT_TYPE_REFRESH_TOKEN,
        @Field("include_policy") includePolicy: Boolean = true
    ): TokenResponse
}