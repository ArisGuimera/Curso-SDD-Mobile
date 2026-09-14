package com.aristidevs.cursopremiumandroid.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Fila de la tabla `dogs`. Guarda tanto el catálogo remoto como los perros que
 * crea el usuario.
 *
 * `weight`, `origin` y `temperament` son anulables porque un perro del catálogo
 * no los tiene hasta que se visita su detalle por primera vez. Un perro creado
 * nace con los tres rellenos.
 */
@Entity(tableName = "dogs")
data class DogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val breed: String,
    val age: Int,
    val description: String,
    val image: String,
    val weight: String?,
    val origin: String?,
    val temperament: String?,
    val isUserCreated: Boolean
)
