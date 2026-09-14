# PLAN.md — Añadir perros con persistencia local (Room)

Plan técnico para `SPEC.md` (cerrada). Este documento no repite el
comportamiento acordado: solo dice cómo se construye y cómo se valida.

## Ajustes aplicados a SPEC.md

Aprobados y ya reflejados en `SPEC.md`, para que los dos documentos digan lo mismo:

1. **`isCatalogSeeded(): Boolean` sustituido por `seedCatalogIfNeeded()`.** La UI
   no necesita preguntar por el estado del seed: necesita que ocurra. Un método
   que siembra si hace falta y no lanza excepción al terminar bien cubre las dos
   cosas y evita una carrera entre consultar y sembrar. Añade `SeedCatalogUseCase`.
2. **`room-ktx` no se añade.** Desde Room 2.7 el soporte de corrutinas y `Flow`
   está en `room-runtime`. **Comprobado en T-01**: la consulta que devuelve
   `Flow` compila con `androidx.room.coroutines.createFlow` y `room-ktx` no
   aparece en `debugRuntimeClasspath`.

## Dependencias

Versiones comprobadas, no supuestas:

| Artefacto | Versión | Cómo se ha comprobado |
| --- | --- | --- |
| `androidx.room:room-runtime` | `2.8.5` | Última estable en `maven-metadata.xml` de Google Maven (`<release>2.8.5</release>`, 2026-09-09). |
| `androidx.room:room-compiler` | `2.8.5` | POM publicado (HTTP 200). Se aplica con el KSP ya presente. |
| `org.jetbrains.kotlinx:kotlinx-coroutines-test` | `1.10.2` | Es la versión a la que resuelve hoy `kotlinx-coroutines-core` en `debugRuntimeClasspath`, vía el BOM de corrutinas. Se fija igual para no desalinear test y runtime. |

Nada más. En concreto **no** se añaden `room-testing` (no hay migraciones que
probar), ni `hilt-android-testing` (los tests de UI se hacen sobre los
composables sin estado, que no necesitan inyección), ni mockk ni turbine (los
dobles se escriben a mano, que es lo que ya permite el diseño por interfaces).

Declarar en `gradle/libs.versions.toml` y consumir en `app/build.gradle.kts`:
`implementation(libs.androidx.room.runtime)`, `ksp(libs.androidx.room.compiler)`,
`testImplementation(libs.kotlinx.coroutines.test)`.

**Riesgo principal del plan: descartado en T-01.** Room 2.8.5 compila con el KSP
`2.3.10` y Kotlin `2.2.10` de este proyecto sin ningún ajuste; `:app:assembleDebug`
genera `DogDatabase_Impl` y `DogDao_Impl`.

## Capas y archivos

`(N)` nuevo · `(M)` modificado. Rutas relativas a
`app/src/main/java/com/aristidevs/cursopremiumandroid/`.

**Datos**

- `(N) data/db/entity/DogEntity.kt` — tabla `dogs`.
- `(N) data/db/DogDao.kt`
- `(N) data/db/DogDatabase.kt` — `version = 1`, `exportSchema = false`.
- `(N) data/mapper/DogEntityMapper.kt` — entidad ↔ dominio.
- `(M) data/DogRepositoryImpl.kt` — recibe `DogDao` además de `DogApiServices`.

**Dominio**

- `(N) domain/model/NewDog.kt`
- `(N) domain/usecase/AddDogUseCase.kt`
- `(N) domain/usecase/SeedCatalogUseCase.kt`
- `(M) domain/DogRepository.kt`
- `(M) domain/usecase/GetDogsUseCase.kt` — devuelve `Flow<List<Dog>>`.

**Presentación**

- `(N) presentation/common/DogAvatar.kt` — compartido por lista y detalle. El
  plan lo situaba dentro de `DogScreen.kt`, pero eso obligaría a
  `DetailScreen.kt` a importar desde `presentation.list`.
- `(N) presentation/add/AddDogViewModel.kt`
- `(N) presentation/add/AddDogScreen.kt`
- `(M) presentation/list/DogViewModel.kt`
- `(M) presentation/list/DogScreen.kt`
- `(M) presentation/detail/DogDetailViewModel.kt` — añade `retry()`.
- `(M) presentation/detail/DetailScreen.kt` — botón Reintentar y placeholder.

**Core**

- `(N) core/di/DatabaseModule.kt`
- `(M) core/di/DataModule.kt` — `provideDogRepository(api, dao)`.
- `(M) core/navigation/Routes.kt` — `@Serializable data object AddDog : NavKey`.
- `(M) core/navigation/AppNavigation.kt` — entrada de `AddDog`.

**Build**

- `(M) gradle/libs.versions.toml`, `(M) app/build.gradle.kts`.

No se toca `data/api/`, `data/api/response/`, `data/mapper/DogMapper.kt`,
`core/di/DogApiConfig.kt` ni `ui/theme/`.

## Persistencia

```kotlin
@Entity(tableName = "dogs")
data class DogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val breed: String,
    val age: Int,
    val description: String,
    val image: String,          // "" en los perros creados
    val weight: String?,        // null hasta que se cachea el detalle
    val origin: String?,
    val temperament: String?,
    val isUserCreated: Boolean
)
```

El catálogo se inserta con su `id` remoto (1–10). Un perro creado se inserta
con `id = 0`, que es lo que dispara la autogeneración de Room: SQLite le asigna
siempre un valor por encima del máximo existente, así que nunca choca con el
catálogo y la ruta `DogDetail(id: Int)` sigue sirviendo para ambos orígenes.

Consulta de la lista, que implementa el orden acordado en una sola sentencia:

```sql
SELECT * FROM dogs
ORDER BY isUserCreated DESC,
         CASE WHEN isUserCreated = 1 THEN -id ELSE id END ASC
```

`isUserCreated DESC` pone primero los creados; el `CASE` invierte el signo solo
para ellos, de modo que los creados salen del más reciente al más antiguo y el
catálogo conserva su orden natural 1→10.

Resto del DAO: `observeDogs(): Flow<List<DogEntity>>`,
`suspend fun getDogById(id: Int): DogEntity?`,
`suspend fun countCatalogDogs(): Int`,
`@Insert suspend fun insertCatalog(dogs: List<DogEntity>)` (una sola llamada,
que Room ejecuta en transacción, así que un seed a medias no es posible),
`@Insert suspend fun insertDog(dog: DogEntity): Long`,
`@Query suspend fun updateDetail(id, weight, origin, temperament)`.

Base de datos `dogs.db`, versión 1, `exportSchema = false`: no hay migraciones
en el alcance y exportar el esquema obligaría a añadir el plugin de Room y a
versionar un JSON que hoy nadie consume. La contrapartida, y hay que decirla:
**el primer cambio de esquema que venga después tendrá que resolver a la vez la
migración y la exportación**, sin un esquema v1 de referencia. Nunca
`fallbackToDestructiveMigration()`: borraría los perros del usuario.

## Flujo de datos

Room es la única fuente de la UI. La red solo escribe en Room.

**Seed** — `DogViewModel.init` → `SeedCatalogUseCase` → `seedCatalogIfNeeded()`:

```
if (dao.countCatalogDogs() > 0) return        // ya sembrado: ni una petición
val dogs = api.getDogs()                       // puede lanzar
dao.insertCatalog(dogs.map { it.toDomain().toEntity() })
```

Se reutiliza `DogResponse.toDomain()` de `data/mapper/DogMapper.kt` para no
duplicar la composición de la URL de imagen con `DogApiConfig.BASE_URL`.

**Lista** — `dao.observeDogs()` → `Flow<List<Dog>>`. Al insertar un perro, Room
reemite y la lista se actualiza sola: no hace falta recargar ni notificar desde
la pantalla del formulario.

**Detalle** — `getDogDetail(id)`:

```
val cached = dao.getDogById(id) ?: error de perro inexistente
cached.toDomainDetail()?.let { return it }     // ya completo (creado, o visitado)
val remote = api.getDogDetail(id)              // puede lanzar
dao.updateDetail(id, remote.weight, remote.origin, remote.temperament)
return dao.getDogById(id)!!.toDomainDetail()!!
```

`DogEntity.toDomainDetail(): DogDetailModel?` devuelve `null` si falta alguno de
los tres campos anulables. Así `DogDetailModel` conserva sus `String` no nulos y
la nulabilidad se queda en la capa de datos, donde pertenece. Un perro creado
nunca llega a la llamada de red porque sus tres campos están rellenos.

**Alta** — `AddDogUseCase(NewDog)` → `dao.insertDog(...)` con `id = 0`,
`image = ""`, `isUserCreated = true`.

## Estado de UI

**Lista** — `DogsUiState` gana un campo:

```kotlin
data class DogsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val dogs: List<Dog> = emptyList(),
    val query: String = "",
    val canAddDog: Boolean = false
)
```

El ViewModel arranca dos corrutinas en `init`: una observa `getDogsUseCase()` y
vuelca la lista filtrada por la `query` vigente; otra ejecuta el seed y decide
`isLoading` / `error` / `canAddDog`. Separarlas importa: la observación no debe
morir porque el seed falle, y así al pulsar Reintentar solo se relanza el seed.
`retry()` repite esa segunda corrutina.

`allDogs` deja de ser un `var` cargado una vez y pasa a ser el último valor
emitido por el flujo; `onQueryChange` filtra sobre él, de modo que la búsqueda
cubre catálogo y creados sin tocar su lógica actual.

**Formulario** — `AddDogUiState` con tres partes:

```kotlin
@Serializable
data class AddDogForm(               // lo que escribe el usuario
    val name: String = "", val breed: String = "", val age: String = "",
    val description: String = "", val weight: String = "",
    val origin: String = "", val temperament: String = ""
)

data class AddDogUiState(
    val form: AddDogForm = AddDogForm(),
    val errors: Map<AddDogField, String> = emptyMap(),
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val focusTarget: AddDogField? = null,   // primer campo con error, evento de un solo uso
    val showDiscardDialog: Boolean = false
)
```

La edad se mantiene como `String` en el formulario y solo se convierte a `Int`
al validar: guardarla como `Int` obligaría a inventar un valor para el campo
vacío y haría imposible distinguir "no escrito" de "cero".

**Supervivencia del estado.** `form` se serializa con kotlinx.serialization
—plugin ya aplicado— y se guarda como `String` bajo una única clave del
`SavedStateHandle` en cada cambio. Es un tipo admitido por `Bundle` sin
ambigüedad, y `SavedStateHandle` es construible en un test de JVM, así que
AC-19 se puede probar sin dispositivo además de en él. Los errores **no** se
persisten a propósito: sobreviven a la rotación porque el ViewModel sobrevive
(AC-18), y tras morir el proceso no tiene sentido acusar al usuario de un error
que ya no ve (AC-19 solo exige recuperar los valores).

**Validación**, al pulsar Guardar y solo entonces:

- Los siete campos, recortados: vacío → "Este campo es obligatorio".
- Edad además: no entero → "La edad debe ser un número"; fuera de 0–30 →
  "La edad debe estar entre 0 y 30".
- Si hay errores, `focusTarget` apunta al primero en orden de pantalla; la
  pantalla lo consume con un `FocusRequester` y lo limpia.
- **Antiduplicado**: `isSaving` se pone a `true` antes de insertar y deshabilita
  el botón. La guarda está en el ViewModel, no solo en el composable, porque un
  botón deshabilitado en recomposición no es una garantía (AC-15).

**Navegación y descarte.** `AddDogScreen` registra un
`BackHandler(enabled = form.hasContent() && !showDiscardDialog)` que abre el
diálogo en lugar de salir; el botón atrás de la `TopAppBar` invoca el mismo
camino, para que gesto y botón no diverjan. Con el formulario vacío el
`BackHandler` está deshabilitado y `NavDisplay` se lleva el atrás como siempre
(AC-17). Tras guardar: `onDogAdded()` → `backstack.removeLastOrNull()` desde
`AppNavigation.kt`, y `DogViewModel.onQueryChange("")` para que el perro nuevo
sea visible aunque hubiera una búsqueda escrita.

**Riesgo a verificar en T-08:** que el `BackHandler` de la pantalla tenga
prioridad sobre el manejador interno de `NavDisplay`. Debería, por registrarse
después en el `OnBackPressedDispatcher`, pero es una suposición sobre
Navigation3 `1.1.5` y hay que comprobarla en el dispositivo, no razonarla.

**Placeholder de imagen.** Composable reutilizable `DogAvatar(name, image, ...)`
en `presentation/list/DogScreen.kt`: si `image` está vacío pinta un `Box` con
`BackgroundComponentSelected` —ya definido en `ui/theme/Color.kt` y hoy sin
usar— y la inicial del nombre; si no, el `AsyncImage` de siempre. El
`contentDescription` es el nombre del perro en ambos casos. La forma
(círculo en la lista, rectángulo redondeado en el detalle) entra por parámetro.

## Errores

`DogRepositoryImpl` deja subir las excepciones; quien las traduce a estado de UI
es el ViewModel, como ya hace `DogDetailViewModel`. En los tres `catch`
(seed, detalle, guardado) se relanza `CancellationException` antes de tratar
nada más — el código actual no lo hace, y es una corrección necesaria para que
salir de la pantalla no se pinte como un error.

Los mensajes son fijos y en español, no `e.message`: hoy
`DogViewModel` y `DogDetailViewModel` muestran el mensaje de la excepción, que
puede filtrar URLs y nombres de clase. Seed y detalle → "No se pudo conectar.
Comprueba tu conexión."; guardado → "No se pudo guardar el perro.".

## Orden de implementación

Cada paso deja el proyecto compilando.

1. **T-01 Dependencias y esqueleto.** Room en el catálogo, `DogEntity` mínima,
   `DogDatabase`, `DogDao` vacío, `DatabaseModule`. Compilar. Es la puerta de
   entrada: aquí se sabe si Room 2.8.5 convive con este KSP y si `room-ktx`
   hace falta.
2. **T-02 DAO completo + `DogEntityMapper`** y su test instrumentado.
3. **T-03 Contratos de dominio**: `NewDog`, `getDogs()` a `Flow`,
   `seedCatalogIfNeeded()`, `addDog()`, y los dos casos de uso nuevos.
4. **T-04 `DogRepositoryImpl`**: seed, lista reactiva, caché de detalle, alta.
5. **T-05 Lista**: `DogViewModel` con seed y reintento, `DogScreen` con FAB,
   distintivo, `DogAvatar` y Reintentar.
6. **T-06 Detalle**: caché progresiva y reintento.
7. **T-07 `AddDogViewModel`**: formulario, validación, `SavedStateHandle`,
   antiduplicado.
8. **T-08 `AddDogScreen` + navegación**: ruta, diálogo de descarte,
   `BackHandler`, vuelta a la lista.
9. **T-09 Tests automáticos** (los de T-02 ya están).
10. **T-17 Validación manual en dispositivo** y registro de evidencias.

El orden va de dentro afuera a propósito: T-03 cambia la firma de `getDogs()`,
que rompe `GetDogsUseCase` y `DogViewModel` a la vez, y conviene que eso pase
pronto y con el repositorio ya listo detrás.

`TASKS.md` desglosa esto con dependencias y comprobaciones.

## Estrategia de pruebas

**JVM** (`app/src/test/`), con dobles escritos a mano:

- `FakeDogApiServices` — implementa `DogApiServices`, cuenta llamadas y permite
  configurar fallos. El contador es lo que demuestra AC-02 y AC-07: no basta
  con que la pantalla se vea bien, hay que probar que no hubo petición.
- `FakeDogDao` — implementa `DogDao` sobre un `MutableStateFlow<List<DogEntity>>`,
  replicando el orden de la consulta SQL.
- `DogRepositoryImplTest`, `DogViewModelTest`, `AddDogViewModelTest`.

`FakeDogDao` tiene una trampa conocida: reimplementa en Kotlin el `ORDER BY`
del DAO, así que si la sentencia SQL cambia, el fake puede seguir en verde
mintiendo. Por eso el orden se prueba **también** en `DogDaoTest`, contra Room
de verdad.

**Instrumentados** (`app/src/androidTest/`):

- `DogDaoTest` con `Room.inMemoryDatabaseBuilder`: orden, autogeneración de id
  por encima del catálogo, `countCatalogDogs`, `updateDetail`.
- `AddDogScreenTest` y `DogScreenTest` con `createComposeRule()` sobre los
  composables **sin estado** (`AddDogContent`, `DogContent`), a los que se les
  pasa el estado a mano. Así no hace falta Hilt en los tests, que es la razón
  por la que el proyecto separa pantalla con estado y contenido sin estado.

**En dispositivo físico** (Pixel 8a, Android 17), porque no hay test que lo
sustituya: modo avión en sus cinco variantes (AC-03, AC-04, AC-05, AC-08,
AC-13), rotación (AC-18), muerte de proceso (AC-14, AC-19), TalkBack (AC-22) y
tamaño de fuente máximo con teclado abierto (AC-23).

**Comandos** (`AGENTS.md`), a ejecutar antes de dar nada por terminado:
`./gradlew :app:assembleDebug`, `./gradlew :app:testDebugUnitTest`,
`./gradlew :app:lintDebug` y, con dispositivo conectado,
`./gradlew :app:connectedDebugAndroidTest`.

## Subagentes

No hacen falta. La feature es un único módulo con unas dos docenas de archivos
y una cadena de dependencias estrictamente secuencial —el repositorio no se
puede escribir antes que el DAO, ni los ViewModels antes que el repositorio—,
así que repartirla costaría más contexto del que ahorraría. Ejecución directa.
