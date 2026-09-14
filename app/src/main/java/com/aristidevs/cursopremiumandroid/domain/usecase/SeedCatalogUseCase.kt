package com.aristidevs.cursopremiumandroid.domain.usecase

import com.aristidevs.cursopremiumandroid.domain.DogRepository
import jakarta.inject.Inject

class SeedCatalogUseCase @Inject constructor(private val repository: DogRepository) {

    suspend operator fun invoke() {
        repository.seedCatalogIfNeeded()
    }
}
