package com.aristidevs.cursopremiumandroid.domain.model

/** Datos que introduce el usuario al crear un perro. Sin id ni imagen. */
data class NewDog(
    val name: String,
    val breed: String,
    val age: Int,
    val description: String,
    val weight: String,
    val origin: String,
    val temperament: String
)
