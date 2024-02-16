package com.net.capp

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("state")
    fun getStateData(): Call<StateResponse>

    @GET("gentnews/{userId}")
    fun getHomeData(@Path("userId") userId: String): Call<data_class_home>

    @POST("searchnews")
    fun getsearchData(@Body request: searchedItem): Call<data_class_search>

    @POST("toggleFollow")
    fun follow(@Body request: toggleFollow): Call<LikeResponse>

    @GET("newsdetais/{newsId}/{userId}")
    fun getNewsDetail(@Path("newsId") newsId: String, @Path("userId") userId: String): Call<data_class_NewsDetail>

    @GET("getUserProfile/{userId}")
    fun getuserDetails(@Path("userId") userId: String): Call<UserData>

    @GET("gentusernews/{userId}")
    fun getUserData(@Path("userId") userId:String): Call<getUserNews>

    @GET("gentusernews/{userId}")
    fun getUserNewsData(@Path("userId") userId: String): Call<data_class_NewsDetail>

    @POST("likenewsdata")
    fun postlike(@Body request: postlike): Call<LikeResponse>

    @POST("commentalldata/{newsId}")
    fun getComments(@Path("newsId") newsId: String): Call<commentData>

    @POST("commentalldelete/{commentId}")
    fun deleteComment(@Path("commentId") commentId: String): Call<isSuccess>

    @POST("likedata")
    fun likeComment(@Body request: postlikeComment): Call<isSuccess>

    @POST("newsimage")
    fun postNews(@Body request: postNews): Call<isSuccess>

    @POST("updatenews")
    fun updateNews(@Body request: updateNews): Call<LikeResponse>

    @POST("deletenews/{newsId}")
    fun deleteNews(@Path("newsId") newsId: String): Call<LikeResponse>

    @POST("prifileinsert")
    fun insertProfile(@Body request: profileInsert): Call<profileInsert>

    @FormUrlEncoded
    @POST("city")
    fun getCity (@Field("state") state: String): Call<data_class_response_city>

    @POST("commentdata")
    fun postcomment(@Body request: postComment): Call<LikeResponse>

    @Multipart
    @POST("imageupload2")
    suspend fun uploadImage(@Part image: MultipartBody.Part): List<String>

    @Multipart
    @POST("videoupload")
    suspend fun uploadVideo(@Part image: MultipartBody.Part): String

    @Multipart
    @POST("imageupload2")
    suspend fun uploadMultiImage(@Part images: ArrayList<MultipartBody.Part>): List<String>

    @GET("deviceid/{uid}/{userId}/{pushToken}")
    fun deviceiddata(@Path("uid") uid: String, @Path("userId") userId: String, @Path("pushToken") pushToken: String): Call<isSuccess>

}
interface Downloader{
    fun downloadFile(url:String) : Long
}