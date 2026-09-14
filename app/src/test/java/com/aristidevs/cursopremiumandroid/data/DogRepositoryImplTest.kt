package com.aristidevs.cursopremiumandroid.data

import com.aristidevs.cursopremiumandroid.domain.model.NewDog
import com.aristidevs.cursopremiumandroid.fake.FakeDogApiServices
import com.aristidevs.cursopremiumandroid.fake.FakeDogDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class DogRepositoryImplTest {

    private lateinit var api: FakeDogApiServices
    private lateinit var dao: FakeDogDao
    private lateinit var repository: DogRepositoryImpl

    @Before
    fun setUp() {
        api = FakeDogApiServices()
        dao = FakeDogDao()
        repository = DogRepositoryImpl(api, dao)
    }

    // AC-01
    @Test
    fun `el seed descarga el catalogo una vez y lo guarda`() = runTest {
        repository.seedCatalogIfNeeded()

        assertEquals(1, api.dogsCalls)
        assertEquals(2, dao.current.size)
        assertTrue(dao.current.none { it.isUserCreated })
        assertTrue(dao.current.all { it.weight == null })
    }

    // AC-02
    @Test
    fun `con el catalogo ya sembrado el seed no vuelve a pedir la lista`() = runTest {
        repository.seedCatalogIfNeeded()
        repository.seedCatalogIfNeeded()
        repository.seedCatalogIfNeeded()

        assertEquals(1, api.dogsCalls)
        assertEquals(2, dao.current.size)
    }

    // AC-03: si la red falla no queda nada a medias y el seed sigue pendiente
    @Test
    fun `si falla la descarga no se guarda nada y el seed sigue pendiente`() = runTest {
        api.failDogs = true

        runCatching { repository.seedCatalogIfNeeded() }

        assertTrue(dao.current.isEmpty())
        assertEquals(0, dao.countCatalogDogs())
    }

    // AC-01: la url de imagen se compone con la base, reutilizando el mapper de red
    @Test
    fun `la imagen del catalogo se guarda como url absoluta`() = runTest {
        repository.seedCatalogIfNeeded()

        assertTrue(dao.current.all { it.image.startsWith("https://") })
    }

    // AC-06
    @Test
    fun `el primer detalle se pide a la red y se persiste`() = runTest {
        repository.seedCatalogIfNeeded()

        val detail = repository.getDogDetail(1)

        assertEquals(1, api.detailCalls)
        assertEquals("28 kg", detail.weight)
        assertEquals("Escocia", dao.current.first { it.id == 1 }.origin)
    }

    // AC-07
    @Test
    fun `el segundo detalle sale de la cache sin tocar la red`() = runTest {
        repository.seedCatalogIfNeeded()
        repository.getDogDetail(1)

        val cached = repository.getDogDetail(1)

        assertEquals(1, api.detailCalls)
        assertEquals("Amigable", cached.temperament)
    }

    // AC-07: cachear un detalle no arrastra los demás
    @Test
    fun `cachear un detalle no afecta a los otros perros`() = runTest {
        repository.seedCatalogIfNeeded()
        repository.getDogDetail(1)

        assertNull(dao.current.first { it.id == 2 }.weight)
    }

    // AC-08
    @Test
    fun `un detalle no cacheado sin red propaga el error`() = runTest {
        repository.seedCatalogIfNeeded()
        api.failDetail = true

        val result = runCatching { repository.getDogDetail(1) }

        assertTrue(result.exceptionOrNull() is IOException)
        assertNull(dao.current.first { it.id == 1 }.weight)
    }

    // AC-12
    @Test
    fun `el perro creado se guarda marcado y sin imagen`() = runTest {
        repository.seedCatalogIfNeeded()

        repository.addDog(newDog())

        val created = dao.current.first { it.isUserCreated }
        assertEquals("Toby", created.name)
        assertEquals("", created.image)
        assertTrue(created.id > 2)
    }

    // AC-13
    @Test
    fun `el detalle de un perro creado no toca la red`() = runTest {
        repository.seedCatalogIfNeeded()
        repository.addDog(newDog())
        val id = dao.current.first { it.isUserCreated }.id

        val detail = repository.getDogDetail(id)

        assertEquals(0, api.detailCalls)
        assertEquals("12 kg", detail.weight)
        assertEquals("Inglaterra", detail.origin)
    }

    // AC-20
    @Test
    fun `la lista pone los creados primero y el catalogo en orden`() = runTest {
        repository.seedCatalogIfNeeded()
        repository.addDog(newDog(name = "Primero"))
        repository.addDog(newDog(name = "Segundo"))

        val dogs = repository.getDogs().first()

        assertEquals(listOf("Segundo", "Primero", "Luna", "Max"), dogs.map { it.name })
        assertTrue(dogs[0].isUserCreated)
        assertFalse(dogs[2].isUserCreated)
    }

    private fun newDog(name: String = "Toby") = NewDog(
        name = name,
        breed = "Beagle",
        age = 4,
        description = "Curioso",
        weight = "12 kg",
        origin = "Inglaterra",
        temperament = "Alegre"
    )
}
