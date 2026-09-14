package com.aristidevs.cursopremiumandroid.domain.usecase

import com.aristidevs.cursopremiumandroid.domain.DogRepository
import com.aristidevs.cursopremiumandroid.domain.model.Dog
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetDogsUseCase @Inject constructor(private val repository: DogRepository) {

    operator fun invoke(): Flow<List<Dog>> {
        return repository.getDogs()
    }
}
