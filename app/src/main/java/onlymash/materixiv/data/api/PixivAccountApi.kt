package onlymash.materixiv.data.api

import okhttp3.ResponseBody
import onlymash.materixiv.app.Keys
import retrofit2.Response
import retrofit2.http.*

interface PixivAccountApi {

    @FormUrlEncoded
    @POST("api/provisional-accounts/create")
    suspend fun signUp(
        @Header(Keys.AUTHORIZATION) auth: String,
        @Field("user_name") username: String,
        @Field("ref") ref: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("api/account/edit")
    suspend fun updateInfo(
        @Header(Keys.AUTHORIZATION) auth: String,
        @FieldMap infoMap: Map<String, String>
    ): Response<ResponseBody>
}