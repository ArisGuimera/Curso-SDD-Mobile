package com.aristidevs.cursopremiumandroid.domain

/** Se pide el detalle de un perro que no está en la base de datos local. */
class DogNotFoundException(id: Int) : Exception("No existe ningún perro con id $id")
