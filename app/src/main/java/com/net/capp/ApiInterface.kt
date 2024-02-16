package com.net.capp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("login")
    fun sendOtp(@Body request: data_class_login): Call<OtpResponce>

    @POST("otp_verify")
    fun verifyOtp(@Body request: data_class_verifyOtp): Call<OtpResponce>
}

