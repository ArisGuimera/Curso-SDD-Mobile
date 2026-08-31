package com.aristidevs.cursopremiumandroid.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class DogResponse(
    val id: Int, val name: String, val age: Int, val description: String, val image: String, val breed:String
)