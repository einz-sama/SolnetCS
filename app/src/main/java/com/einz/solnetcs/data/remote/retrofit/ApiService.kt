package com.einz.solnetcs.data.remote.retrofit

import com.einz.solnetcs.data.remote.responses.ResponseGetCustomer
import com.einz.solnetcs.data.remote.responses.ResponseGetTicket
import com.einz.solnetcs.data.remote.responses.ResponseLogin
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface ApiService {

    @FormUrlEncoded
    @POST("index.php/login")
    suspend fun login(
        @Field("username")username:String,
        @Field("password")password:String
    ): Response<ResponseLogin>

    @GET("index.php/get_customer?id_customer")
    suspend fun getCustomer(
        @Query("id_customer")id_customer:String
    ): Response<ResponseGetCustomer>

    @GET("index.php/get_keluhan?ID_Customer")
    suspend fun getTicket(
        @Query("ID_Customer")id_customer:String
    ): Response<ResponseGetTicket>


}