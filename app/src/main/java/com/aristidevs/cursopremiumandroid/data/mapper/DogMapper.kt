package com.aristidevs.cursopremiumandroid.data.mapper

import com.aristidevs.cursopremiumandroid.core.di.DogApiConfig.BASE_URL
import com.aristidevs.cursopremiumandroid.data.api.response.DogDetailResponse
import com.aristidevs.cursopremiumandroid.data.api.response.DogResponse
import com.aristidevs.cursopremiumandroid.domain.model.Dog
import com.aristidevs.cursopremiumandroid.domain.model.DogDetailModel

fun DogResponse.toDomain(): Dog {
    return Dog(
        id = id,
        name = name,
        description = description,
        age = age,
        image = BASE_URL + image,
        breed = breed
    )
}

fun DogDetailResponse.toDomain(): DogDetailModel {
    return DogDetailModel(
        id = id,
        name = name,
        breed = breed,
        age = age,
        description = description,
        image = BASE_URL + image,
        weight = weight,
        origin = origin,
        temperament = temperament
    )
}