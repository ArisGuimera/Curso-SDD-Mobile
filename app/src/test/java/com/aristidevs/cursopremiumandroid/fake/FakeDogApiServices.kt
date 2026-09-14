package com.aristidevs.cursopremiumandroid.fake

import com.aristidevs.cursopremiumandroid.data.api.DogApiServices
import com.aristidevs.cursopremiumandroid.data.api.response.DogDetailResponse
import com.aristidevs.cursopremiumandroid.data.api.response.DogResponse
import java.io.IOException

/**
 * Los contadores son lo que permite demostrar que una segunda lectura NO va a
 * la red. Sin ellos, una pantalla correcta no probaría nada sobre la caché.
 */
class FakeDogApiServices : DogApiServices {

    var dogsCalls = 0
        private set
    var detailCalls = 0
        private set

    var failDogs = false
    var failDetail = false

    var dogs: List<DogResponse> = listOf(
        DogResponse(1, "Luna", 3, "Cariñosa", "images/luna.jpg", "Golden Retriever"),
        DogResponse(2, "Max", 5, "Juguetón", "images/max.jpg", "Labrador Retriever")
    )

    var details: Map<Int, DogDetailResponse> = mapOf(
        1 to DogDetailResponse(
            1, "Luna", "Golden Retriever", 3, "Cariñosa", "images/luna.jpg",
            "28 kg", "Escocia", "Amigable"
        ),
        2 to DogDetailResponse(
            2, "Max", "Labrador Retriever", 5, "Juguetón", "images/max.jpg",
            "32 kg", "Canadá", "Activo"
        )
    )

    override suspend fun getDogs(): List<DogResponse> {
        dogsCalls++
        if (failDogs) throw IOException("sin conexión")
        return dogs
    }

    override suspend fun getDogDetail(id: Int): DogDetailResponse {
        detailCalls++
        if (failDetail) throw IOException("sin conexión")
        return details.getValue(id)
    }
}
