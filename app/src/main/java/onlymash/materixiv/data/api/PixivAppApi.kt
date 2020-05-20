package onlymash.materixiv.data.api

import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import onlymash.materixiv.app.Keys.AUTHORIZATION
import onlymash.materixiv.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface PixivAppApi {

    @GET("v2/search/autocomplete")
    suspend fun autocomplete(
        @Header(AUTHORIZATION) auth: String,
        @Query("word") word: String?,
        @Query("merge_plain_keyword_results") marge: Boolean = true,
        @Query("include_translated_tag_results") includeTranslated: Boolean = true,
        @Query("filter") filter: String = "for_android"
    ): Response<TagResponse>

    @GET
    suspend fun getUsers(
        @Header(AUTHORIZATION) auth: String,
        @Url url: HttpUrl
    ): Response<UserResponse>

    @GET("v1/user/detail?filter=for_android")
    suspend fun getUserDetail(
        @Header(AUTHORIZATION) auth: String,
        @Query("user_id") userId: String,
        @Query("filter") filter: String = "for_android"
    ): Response<UserDetailResponse>

    @FormUrlEncoded
    @POST("v1/user/follow/add")
    suspend fun addFollowUser(
        @Header(AUTHORIZATION) auth: String,
        @Field("user_id") userId: Long,
        @Field("restrict") restrict: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("v1/user/follow/delete")
    suspend fun deleteFollowUser(
        @Header(AUTHORIZATION) auth: String,
        @Field("user_id") userId: Long
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("v2/illust/bookmark/add")
    suspend fun addBookmarkIllust(
        @Header(AUTHORIZATION) auth: String,
        @Field("illust_id") illustId: Long,
        @Field("restrict") restrict: String,
        @Field("tags[]") tags: List<String>? = null
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("v1/illust/bookmark/delete")
    suspend fun deleteBookmarkIllust(
        @Header(AUTHORIZATION) auth: String,
        @Field("illust_id") illustId: Long
    ): Response<ResponseBody>

    @GET
    suspend fun getIllusts(
        @Header(AUTHORIZATION) auth: String,
        @Url url: HttpUrl
    ): Response<IllustResponse>

    @GET("v1/illust/detail")
    suspend fun getIllustDetail(
        @Header(AUTHORIZATION) auth: String,
        @Query("illust_id") illustId: Long,
        @Query("filter") filter: String = "for_android"
    ): Response<IllustDetailResponse>

    @GET("v1/illust/comments")
    suspend fun getIllustComments(
        @Header(AUTHORIZATION) auth: String,
        @Query("illust_id") illustId: Long
    ): Response<CommentResponse>

    @GET
    suspend fun getIllustCommentsNext(
        @Header(AUTHORIZATION) auth: String,
        @Url url: String
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("v1/illust/comment/add")
    suspend fun addIllustComment(
        @Header(AUTHORIZATION) auth: String,
        @Field("illust_id") illustId: Long,
        @Field("comment") comment: String,
        @Field("parent_comment_id") parentCommentId: Int? = null
    ): Response<ResponseBody>

    @GET("v1/trending-tags/illust")
    suspend fun getTrendTagsIllust(
        @Header(AUTHORIZATION) auth: String,
        @Query("filter") filter: String = "for_android"
    ): Response<TrendTagResponse>

    @GET("v1/trending-tags/novel")
    suspend fun getTrendTagsNovel(
        @Header(AUTHORIZATION) auth: String,
        @Query("filter") filter: String = "for_android"
    ): Response<TrendTagResponse>

    @GET
    suspend fun getNovels(
        @Header(AUTHORIZATION) auth: String,
        @Url url: HttpUrl
    ): Response<NovelResponse>

    @GET("v1/ugoira/metadata")
    suspend fun getUgoiraMetadata(
        @Header(AUTHORIZATION) auth: String,
        @Query("illust_id") illustId: Long
    ): Response<UgoiraResponse>

    @GET("v1/user/bookmark-tags/illust")
    suspend fun getIllustBookmarkTags(
        @Header(AUTHORIZATION) auth: String,
        @Query("user_id") userId: Long,
        @Query("restrict") restrict: String
    )

    @GET("v1/spotlight/articles?filter=for_android")
    suspend fun getPixivisionArticles(
        @Header(AUTHORIZATION) auth: String,
        @Query("category") category: String
    ): Response<ResponseBody>

    @Multipart
    @POST("v1/user/profile/edit")
    suspend fun editUserProfile(
        @Header(AUTHORIZATION) auth: String,
        @Part body: MultipartBody.Part
    ): Response<ResponseBody>


    @GET("v1/user/browsing-history/illusts")
    suspend fun getIllustBrowsingHistory(
        @Header(AUTHORIZATION) auth: String)

    @GET("v1/user/bookmarks/illust")
    suspend fun getIllustBookmark(
        @Header(AUTHORIZATION) auth: String,
        @Query("user_id") userId: Int,
        @Query("restrict") restrict: String,
        @Query("tag") tag: String?
    )
}