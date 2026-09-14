package com.aristidevs.cursopremiumandroid.data.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aristidevs.cursopremiumandroid.data.db.entity.DogEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Contra Room de verdad, no contra FakeDogDao: el orden de la lista depende de
 * una sentencia SQL que el doble reimplementa y podría dejar de reflejar.
 */
@RunWith(AndroidJUnit4::class)
class DogDaoTest {

    private lateinit var database: DogDatabase
    private lateinit var dao: DogDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DogDatabase::class.java).build()
        dao = database.dogDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun catalogDog(id: Int, name: String) = DogEntity(
        id = id, name = name, breed = "Raza $id", age = id, description = "Desc $id",
        image = "https://example.com/$id.jpg", weight = null, origin = null,
        temperament = null, isUserCreated = false
    )

    private fun userDog(name: String) = DogEntity(
        id = 0, name = name, breed = "Beagle", age = 4, description = "Curioso",
        image = "", weight = "12 kg", origin = "Inglaterra",
        temperament = "Alegre", isUserCreated = true
    )

    // AC-20
    @Test
    fun losCreadosVanPrimeroDelMasRecienteAlMasAntiguo() = runTest {
        dao.insertCatalog(listOf(catalogDog(1, "Luna"), catalogDog(2, "Max"), catalogDog(3, "Nala")))
        dao.insertDog(userDog("Primero"))
        dao.insertDog(userDog("Segundo"))

        val names = dao.observeDogs().first().map { it.name }

        assertEquals(listOf("Segundo", "Primero", "Luna", "Max", "Nala"), names)
    }

    // AC-20
    @Test
    fun elCatalogoConservaSuOrdenDeIdAscendente() = runTest {
        dao.insertCatalog(listOf(catalogDog(3, "Nala"), catalogDog(1, "Luna"), catalogDog(2, "Max")))

        val names = dao.observeDogs().first().map { it.name }

        assertEquals(listOf("Luna", "Max", "Nala"), names)
    }

    // AC-12: el id autogenerado nunca choca con el del catálogo
    @Test
    fun elIdDelPerroCreadoQuedaPorEncimaDelCatalogo() = runTest {
        dao.insertCatalog((1..10).map { catalogDog(it, "Perro $it") })

        val id = dao.insertDog(userDog("Toby"))

        assertTrue("id $id debería superar al del catálogo", id > 10)
        assertEquals("Toby", dao.getDogById(id.toInt())?.name)
    }

    // AC-01 / AC-03: el contador de catálogo es la marca de seed completado
    @Test
    fun contarCatalogoIgnoraLosPerrosCreados() = runTest {
        assertEquals(0, dao.countCatalogDogs())

        dao.insertCatalog(listOf(catalogDog(1, "Luna"), catalogDog(2, "Max")))
        assertEquals(2, dao.countCatalogDogs())

        dao.insertDog(userDog("Toby"))

        assertEquals("un perro creado no cuenta como catálogo", 2, dao.countCatalogDogs())
    }

    /**
     * Fija por qué el alta está bloqueada hasta que el seed termina (AC-03): si
     * un perro creado llegara antes que el catálogo, tomaría el id 1 y la
     * siembra reventaría con una violación de clave primaria. La ausencia de
     * colisiones no es una propiedad del esquema, es una consecuencia de esa
     * decisión de producto. Si alguna vez se desbloquea el alta sin catálogo,
     * este test avisa de que hay que cambiar la estrategia de ids.
     */
    @Test
    fun crearUnPerroAntesDeSembrarImpediriaElSeed() = runTest {
        dao.insertDog(userDog("Toby"))

        val result = runCatching { dao.insertCatalog(listOf(catalogDog(1, "Luna"))) }

        assertTrue(
            "se esperaba una colisión de clave primaria",
            result.exceptionOrNull() is SQLiteConstraintException
        )
    }

    // AC-06
    @Test
    fun actualizarElDetalleRellenaSoloElPerroIndicado() = runTest {
        dao.insertCatalog(listOf(catalogDog(1, "Luna"), catalogDog(2, "Max")))

        dao.updateDetail(1, "28 kg", "Escocia", "Amigable")

        val luna = dao.getDogById(1)!!
        assertEquals("28 kg", luna.weight)
        assertEquals("Escocia", luna.origin)
        assertEquals("Amigable", luna.temperament)
        assertNull(dao.getDogById(2)!!.weight)
    }

    // AC-12: la lista reacciona a la inserción sin volver a consultarse
    @Test
    fun elFlujoEmiteDeNuevoAlInsertarUnPerro() = runTest {
        dao.insertCatalog(listOf(catalogDog(1, "Luna")))
        assertEquals(1, dao.observeDogs().first().size)

        dao.insertDog(userDog("Toby"))

        assertEquals(listOf("Toby", "Luna"), dao.observeDogs().first().map { it.name })
    }
}
