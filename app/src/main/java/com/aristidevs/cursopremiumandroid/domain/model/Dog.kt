package com.aristidevs.cursopremiumandroid.domain.model

data class Dog(
    val id: Int,
    val name: String,
    val age: Int,
    val description: String,
    val image: String,
    val breed: String,
    /** Distingue los perros creados en el dispositivo de los que vienen del catálogo. */
    val isUserCreated: Boolean = false
)
