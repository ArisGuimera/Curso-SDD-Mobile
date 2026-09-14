package com.aristidevs.cursopremiumandroid.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aristidevs.cursopremiumandroid.data.db.entity.DogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DogDao {

    /**
     * Primero los creados por el usuario, del más reciente al más antiguo, y
     * después el catálogo en su orden natural. El CASE invierte el signo solo
     * para los creados, cuyos id son crecientes por ser autogenerados.
     */
    @Query(
        """
        SELECT * FROM dogs
        ORDER BY isUserCreated DESC,
                 CASE WHEN isUserCreated = 1 THEN -id ELSE id END ASC
        """
    )
    fun observeDogs(): Flow<List<DogEntity>>

    @Query("SELECT * FROM dogs WHERE id = :id")
    suspend fun getDogById(id: Int): DogEntity?

    /** El seed está pendiente si y solo si esto devuelve 0. */
    @Query("SELECT COUNT(*) FROM dogs WHERE isUserCreated = 0")
    suspend fun countCatalogDogs(): Int

    /** Room envuelve la inserción múltiple en una transacción: no hay seed a medias. */
    @Insert
    suspend fun insertCatalog(dogs: List<DogEntity>)

    @Insert
    suspend fun insertDog(dog: DogEntity): Long

    @Query(
        "UPDATE dogs SET weight = :weight, origin = :origin, temperament = :temperament WHERE id = :id"
    )
    suspend fun updateDetail(id: Int, weight: String, origin: String, temperament: String)
}
