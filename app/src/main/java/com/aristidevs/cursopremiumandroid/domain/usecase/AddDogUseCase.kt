package com.aristidevs.cursopremiumandroid.domain.usecase

import com.aristidevs.cursopremiumandroid.domain.DogRepository
import com.aristidevs.cursopremiumandroid.domain.model.NewDog
import jakarta.inject.Inject

class AddDogUseCase @Inject constructor(private val repository: DogRepository) {

    suspend operator fun invoke(dog: NewDog) {
        repository.addDog(dog)
    }
}
