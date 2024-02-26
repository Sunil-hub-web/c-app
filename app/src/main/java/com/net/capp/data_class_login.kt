package com.net.capp

data class data_class_login(
    val name : String,
    val phone : String,
    val state_id : String,
    val city_id : String,
    val device : Device
)
data class Device(
    val userId : String,
    val pushToken : String,
)

data class data_class_verifyOtp(
    val phone: String,
    val otp: String)

data class Details(
    val id: String,
    val username: String,
    val phone: String,
    val date: String,
    val status: String,
    val gender: String,
    val dob: String,
    val about: String,
    val state: String,
    val city: String,
    val police: String,
    val image: String
)

data class OtpResponce(
    val success: Boolean,
    val user: Details,
    val message: String
)
