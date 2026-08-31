package com.aristidevs.cursopremiumandroid.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class DogDetailResponse(
    val id: Int,
    val name: String,
    val breed: String,
    val age: Int,
    val description: String,
    val image: String,
    val weight: String,
    val origin: String,
    val temperament: String
)