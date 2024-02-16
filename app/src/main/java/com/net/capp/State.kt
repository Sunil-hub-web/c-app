package com.net.capp

data class StateResponse(
    val success: Boolean,
    val state: List<StateItem>
)

data class StateItem(
    val id: String,
    val name: String,
    val status: String,
    val link: String
)

data class data_class_response_city(
    val success: Boolean,
    val city: List<CityItem>
)

data class CityItem(
    val id: String,
    val name: String,
    val status: String,
    val link: String
)