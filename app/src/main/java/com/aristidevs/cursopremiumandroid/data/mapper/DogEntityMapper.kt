package com.aristidevs.cursopremiumandroid.data.mapper

import com.aristidevs.cursopremiumandroid.data.db.entity.DogEntity
import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel
import com.aristidevs.cursopremiumandroid.domain.model.NewDog

fun DogEntity.toDomain(): Dog {
    return Dog(
        id = id,
        name = name,
        age = age,
        description = description,
        image = image,
        breed = breed,
        isUserCreated = isUserCreated
    )
}

/** Null mientras el detalle no esté cacheado, que es lo que dispara la petición de red. */
fun DogEntity.toDomainDetail(): DogDetailModel? {
    val weight = weight ?: return null
    val origin = origin ?: return null
    val temperament = temperament ?: return null
    return DogDetailModel(
        id = id,
        name = name,
        breed = breed,
        age = age,
        description = description,
        image = image,
        weight = weight,
        origin = origin,
        temperament = temperament
    )
}

fun Dog.toCatalogEntity(): DogEntity {
    return DogEntity(
        id = id,
        name = name,
        breed = breed,
        age = age,
        description = description,
        image = image,
        weight = null,
        origin = null,
        temperament = null,
        isUserCreated = false
    )
}

fun NewDog.toEntity(): DogEntity {
    return DogEntity(
        // id = 0 hace que Room autogenere un valor por encima del máximo existente.
        id = 0,
        name = name,
        breed = breed,
        age = age,
        description = description,
        // Sin imagen: la UI pinta el placeholder con la inicial del nombre.
        image = "",
        weight = weight,
        origin = origin,
        temperament = temperament,
        isUserCreated = true
    )
}
