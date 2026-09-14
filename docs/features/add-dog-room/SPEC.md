# SPEC.md — Añadir perros con persistencia local (Room)

<!-- PARA EL AGENTE. Este archivo es la especificación de una feature. Tu
     tarea depende de si las secciones de abajo están vacías o completas:

     SI LAS SECCIONES ESTÁN VACÍAS, tu trabajo es completarlas conmigo, en
     orden, una a la vez. En cada sección: primero busca en el repo lo que
     puedas responder tú (rutas, patrones, qué existe ya) y muéstramelo.
     Después hazme las preguntas que necesitas para el resto, de una en una.
     No pases a la siguiente sección hasta que yo dé esta por cerrada. Cuando
     terminemos, escribe el archivo completo. No escribas código.

     SI LAS SECCIONES ESTÁN COMPLETAS, tu trabajo es construir la feature.
     Antes de escribir código, dime qué te sigue pareciendo ambiguo. Cuando lo
     aclaremos, implementa solo lo que está en el alcance y demuestra cada
     criterio de aceptación con la evidencia que pide "Cómo se demuestra".
     "Debería funcionar" no es evidencia.

     Los comentarios como este son instrucciones para ti. No los borres. -->

<!-- PARA LA PERSONA, NO PARA EL AGENTE. Una SPEC_TEMPLATE.md por feature. Cópiala
     vacía al proyecto y dile al agente «lee .md». Cuando la spec esté
     cerrada, abre una sesión nueva y repite lo mismo: el agente que construye
     no debe arrastrar las dudas del que escribió. -->

---


## Qué construimos

<!-- Una frase. Qué puede hacer el usuario que antes no podía.
     Si necesitas dos frases, probablemente son dos features. -->

El usuario puede crear sus propios perros y consultarlos, junto al catálogo ya
descargado, sin conexión, porque el catálogo, los detalles visitados y los
perros creados se guardan en una base de datos local Room.

## Fuera de alcance

<!-- Va casi al principio a propósito: es lo que evita que el agente se invente
     trabajo a mitad de camino. Si lo dejas vacío, llenará el vacío por su
     cuenta y te enterarás en la review. -->

- Editar y eliminar perros (creados o del catálogo). Solo se crea.
- Imagen propia del perro creado: ni galería, ni cámara, ni campo de URL.
  No se añade ningún permiso nuevo.
- Sincronizar o subir los perros creados a ningún servidor. Son solo locales.
- Refrescar el catálogo: `dogs.json` se descarga una sola vez y no se vuelve a
  consultar nunca. La app no reflejará altas, bajas ni cambios del servidor.
- Refrescar un detalle ya guardado: una vez cacheado no se vuelve a pedir.
- Migraciones de esquema: la base de datos nace en versión 1.
- Paginación, ordenación configurable o filtros nuevos. La búsqueda actual por
  nombre y raza se mantiene tal cual, solo cambia el conjunto sobre el que filtra.
- Traducir la app o mover los textos a `strings.xml`. Se mantiene la convención
  actual: textos en español dentro de los composables.
- Modo claro / tema dinámico. La app es de tema oscuro fijo.

## Cómo encaja en el proyecto

<!-- Esto lo saca el agente del repo. Exige rutas reales, no descripciones:
     "sigue las convenciones del proyecto" no sirve de nada. -->

**Dónde vive:** módulo `app`, paquete `com.aristidevs.cursopremiumandroid`.
Raíz de fuentes: `app/src/main/java/com/aristidevs/cursopremiumandroid/`.

**Se apoya en:** (todo verificado, ya existe)

| Ruta | Qué aporta |
| --- | --- |
| `data/api/DogApiServices.kt` | `getDogs()` y `getDogDetail(id)`. No se modifica. |
| `data/api/response/DogResponse.kt`, `DogDetailResponse.kt` | DTOs del contrato remoto. No se modifican. |
| `data/mapper/DogMapper.kt` | `toDomain()` y composición de la URL de imagen con `DogApiConfig.BASE_URL`. |
| `domain/model/Dog.kt`, `domain/model/DogDetailModel.kt` | `DogDetailModel` se reutiliza tal cual; `Dog` gana `isUserCreated` (ver «Qué se modifica»). |
| `domain/DogRepository.kt` | Contrato que se amplía, no se sustituye. |
| `presentation/list/DogScreen.kt` | `DogContent`, `DogSearchBar`, `DogItem`, `LoadingDogState`, `ErrorDogState`. |
| `presentation/detail/DetailScreen.kt` | `DogDetailSuccessContent` y `DetailRow`, que ya pintan peso (`:152`), origen (`:153`) y temperamento (`:162`). |
| `core/navigation/Routes.kt`, `core/navigation/AppNavigation.kt` | Navigation3 con claves `@Serializable`. |
| `core/di/DataModule.kt` | Módulo Hilt `SingletonComponent` donde ya se construyen Json, Retrofit, API y repositorio. |
| `ui/theme/Color.kt` | `BackgroundApp`, `BackgroundComponent`, `BackgroundComponentSelected` (definido y hoy sin usar), `PrimaryButton`, `SecondaryText`, `ControlColor`. |
| `app/src/main/AndroidManifest.xml` | `INTERNET` ya declarado y `windowSoftInputMode="adjustResize"` ya configurado en `MainActivity`. |
| `gradle/libs.versions.toml` | Catálogo de versiones. Plugin KSP ya aplicado (lo usa Hilt), reutilizable por `room-compiler`. |

**Sigue el patrón de:** la feature Dog completa, de punta a punta:
`data/DogRepositoryImpl.kt` → `domain/usecase/GetDogsUseCase.kt` →
`presentation/list/DogViewModel.kt` → `presentation/list/DogScreen.kt`.
La pantalla nueva replica ese esqueleto: entrada con estado (`hiltViewModel()` +
`collectAsStateWithLifecycle()`) y `*Content` sin estado con callbacks.

## Cómo está hecho por dentro

<!-- Las decisiones que, si no las tomas tú, las toma el agente. Y las suyas
     son siempre las más cómodas para él, no para tu proyecto. -->

**Capas que toca:**

Room pasa a ser la única fuente de verdad de la UI. La red solo escribe en Room.

- Lista: `DogScreen` → `DogViewModel` → `GetDogsUseCase` → `DogRepository` →
  `DogRepositoryImpl` → `DogDao` (lectura reactiva) + `DogApiServices` (solo en el seed).
- Detalle: `DogDetailScreen` → `DogDetailViewModel` → `GetDogDetailUseCase` →
  `DogRepositoryImpl` → `DogDao`; si el detalle no está cacheado y el perro es
  del catálogo, pide `details/{id}.json`, lo guarda y vuelve a leer de Room.
- Alta: `AddDogScreen` → `AddDogViewModel` → `AddDogUseCase` →
  `DogRepositoryImpl` → `DogDao`. No toca la red en ningún momento.

**Qué se crea nuevo:**

- Tabla `dogs`, única. Columnas: `id` (PK, `autoGenerate = true`), `name`,
  `breed`, `age`, `description`, `image`, `weight`, `origin`, `temperament`
  (las tres últimas anulables), `isUserCreated`.
- `data/db/DogDatabase.kt` — `@Database(entities = [DogEntity::class], version = 1)`.
- `data/db/DogDao.kt` — lectura reactiva de la lista, lectura de un perro,
  inserción del catálogo en transacción, actualización de detalle, inserción de perro creado.
- `data/db/entity/DogEntity.kt`.
- `data/mapper/DogEntityMapper.kt` — entidad ↔ dominio, separado de `DogMapper.kt`
  para no mezclar mapeos de red y de base de datos.
- `domain/model/NewDog.kt` — datos que introduce el usuario, sin `id`.
- `domain/usecase/AddDogUseCase.kt` y `domain/usecase/SeedCatalogUseCase.kt`.
- `presentation/add/AddDogViewModel.kt` y `presentation/add/AddDogScreen.kt`.
- Clave de navegación `AddDog` (`@Serializable data object`) en `core/navigation/Routes.kt`.
- `core/di/DatabaseModule.kt` — provee `DogDatabase` y `DogDao` como `@Singleton`.
- Dependencias nuevas: `androidx.room:room-runtime` y `room-compiler` (vía el KSP
  ya aplicado). `room-ktx` no hace falta: desde Room 2.7 el soporte de `Flow` vive
  en `room-runtime`, comprobado al compilar. Versiones exactas, en `PLAN.md`.

**Qué se modifica:**

- `domain/DogRepository.kt`: `getDogs()` pasa de `suspend fun(): List<Dog>` a
  `fun(): Flow<List<Dog>>` para que la lista se actualice sola al insertar.
  Se añaden `suspend fun addDog(dog: NewDog)` y `suspend fun seedCatalogIfNeeded()`,
  que siembra si hace falta y no lanza excepción cuando termina bien; así la UI no
  tiene que consultar el estado del seed y sembrar en dos pasos separados.
  `getDogDetail(id)` mantiene su firma.
- `domain/model/Dog.kt`: gana `isUserCreated: Boolean`. Es necesario: la lista
  tiene que distinguir el origen del perro para ordenarlo y para pintar su
  distintivo (AC-20, AC-22), y esa información no llega a presentación de otra
  forma sin saltarse las capas.
- `data/DogRepositoryImpl.kt`: recibe también `DogDao`; implementa seed, caché de
  detalle y alta.
- `domain/usecase/GetDogsUseCase.kt`: devuelve `Flow<List<Dog>>`.
- `presentation/list/DogViewModel.kt`: observa el flujo del repositorio en lugar
  de una carga puntual; `onQueryChange` filtra sobre la lista combinada.
  `DogsUiState` gana `canAddDog: Boolean`.
- `presentation/list/DogScreen.kt`: FAB de añadir, distintivo textual en `DogItem`,
  placeholder para perros sin imagen y botón Reintentar en `ErrorDogState`.
- `presentation/detail/DetailScreen.kt`: las filas Peso/Origen/Temperamento se
  alimentan igual; se añade Reintentar al estado de error.
- `core/navigation/AppNavigation.kt`: entrada para `AddDog`.
- `core/di/DataModule.kt`: `provideDogRepository` recibe además el `DogDao`.
- `app/build.gradle.kts` y `gradle/libs.versions.toml`: dependencias de Room.

**Contratos:**

- Entrada de red (sin cambios): `dogs.json` → `List<DogResponse>`;
  `details/{id}.json` → `DogDetailResponse`. El campo `image` es una ruta
  relativa que se compone con `DogApiConfig.BASE_URL` en el mapper.
- `NewDog`: `name`, `breed`, `description`, `weight`, `origin`, `temperament`
  (`String`) y `age` (`Int`). Sin `id` y sin `image`.
- Identificadores: los 10 perros del catálogo conservan su `id` remoto (1–10).
  Los creados reciben el `id` autogenerado por Room, siempre mayor que el máximo
  existente, así que no colisionan y la ruta `DogDetail(id: Int)` no cambia.
- `isUserCreated` distingue el origen. Un perro del catálogo tiene
  `weight`, `origin` y `temperament` nulos hasta que se visita su detalle;
  un perro creado los tiene rellenos desde el primer momento.
- **El seed está pendiente si y solo si no hay ningún perro con
  `isUserCreated = false` en la tabla.** No se añade ninguna otra marca de
  estado. La inserción del catálogo es una única transacción, así que un seed
  a medias no es posible, y al estar bloqueada el alta hasta que el seed
  termine, no puede haber perros creados antes del catálogo.
- Imagen del perro creado: se guarda `image` vacío. La UI pinta un placeholder
  con `BackgroundComponentSelected` y la inicial del nombre, círculo en la lista
  y rectángulo en el detalle, con su propio `contentDescription`.

**Prohibido:**

- Gson, Moshi o cualquier otro conversor JSON. Se mantiene kotlinx.serialization.
- Navigation Compose junto a Navigation3.
- DataStore o SharedPreferences para marcar el seed. La regla es la de arriba.
- `fallbackToDestructiveMigration()` y `allowMainThreadQueries()`.
- Entidades, DAOs o DTOs en la capa de presentación, y Room en `domain/`.
- Borrar filas de `dogs` en cualquier flujo de esta feature.
- Cambiar los colores del tema o meter hex sueltos en las pantallas.
- Añadir ktlint, detekt u otra herramienta de calidad.
- Tocar `data/api/DogApiServices.kt` y los `*Response.kt`: el contrato remoto no cambia.

## Qué pasa cuando no sale bien

<!-- El camino feliz lo resuelve cualquiera. Lo que vuelve como bug es esto.
     Las últimas tres filas son la vida real de una app: pasan todos los días
     en el bolsillo del usuario. Si alguna fila no aplica de verdad, escribe
     "no aplica" y por qué; no la dejes vacía. -->

| Situación | Qué tiene que pasar |
|---|---|
| No hay datos | Con el seed pendiente la lista nunca está "vacía": o carga, o error con Reintentar. La única lista vacía real es una búsqueda sin coincidencias, que mantiene el texto actual "No hay resultados" y conserva lo escrito para poder corregirlo. |
| La entrada es inválida | Al pulsar Guardar se marcan los campos vacíos (tras recortar espacios) con su mensaje debajo, y la edad con un mensaje propio si no es un entero de 0 a 30. No se inserta nada y no se navega. El foco se lleva al primer campo con error. |
| Falla algo de lo que depende | Fallo de red o respuesta inválida en el seed: lista en estado de error con Reintentar y alta bloqueada. Fallo al pedir un detalle no cacheado: pantalla de detalle en error con Reintentar; el catálogo sigue intacto. Fallo de escritura en Room al guardar un perro: se permanece en el formulario con los datos intactos y un mensaje de error; no se navega ni se pierde lo escrito. Ningún error muestra excepciones crudas ni rutas internas. |
| Tarda demasiado | Se mantienen los timeouts por defecto de OkHttp; no se añade uno propio. Mientras tanto la lista muestra `LoadingDogState` y el detalle su `CircularProgressIndicator`, y la UI sigue respondiendo porque Room hace el trabajo fuera del hilo principal. El alta no toca la red, así que no puede quedarse colgada. |
| No hay conexión (o se corta a mitad) | Primer arranque sin red: error con Reintentar y sin acceso a crear. Con el seed hecho: la lista completa se ve sin red, los detalles ya visitados también, y los no visitados muestran error con Reintentar. Crear un perro funciona siempre sin red una vez sembrado el catálogo. Si la red se corta en mitad del seed o de la carga de un detalle, no queda nada a medias: la transacción no se aplica y se muestra el error. |
| El usuario sale de la app a mitad de camino | La operación en curso vive en `viewModelScope` y se cancela con la pantalla; `CancellationException` se relanza, nunca se trata como error de usuario. Al volver, la lista y el detalle se recomponen desde Room. Si sale con el formulario relleno por el botón atrás, primero ve la confirmación de descarte; si se va por el botón de inicio o cambiando de app, el formulario sigue igual al volver. |
| El sistema mata el proceso y el usuario vuelve | Lo guardado en Room sigue ahí: catálogo, detalles cacheados y perros creados. El formulario a medio rellenar se recupera con sus valores porque vive en el `SavedStateHandle` del ViewModel, y la pila de navegación se restaura porque las claves de `Routes.kt` son `@Serializable` y `rememberNavBackStack` las guarda. |

**Otros puntos de MOBILE_GUIDELINES aplicados o descartados:**

- **Accesibilidad:** el distintivo del perro creado es texto, no solo color;
  el placeholder de imagen lleva `contentDescription`; los campos del formulario
  tienen etiqueta y sus errores se asocian al campo, no solo al color rojo.
- **Pantallas:** el formulario es desplazable para que con el teclado abierto y
  con el tamaño de fuente del sistema al máximo sigan siendo alcanzables los
  7 campos y el botón Guardar. Orientación libre, como hasta ahora.
- **Interacción:** el botón Guardar se deshabilita mientras se inserta, para que
  pulsaciones repetidas no creen perros duplicados.
- **Rendimiento:** la lista sigue siendo `LazyColumn` con `key = dog.id`.
- **Permisos:** no aplica. No se añade ninguno; `INTERNET` ya está declarado.
- **Trabajo en segundo plano:** no aplica. No hay tareas diferidas ni WorkManager;
  el seed ocurre en primer plano, ligado a la pantalla de lista.
- **Idiomas y formatos:** no aplica más allá de lo existente. La app no está
  localizada y la edad es un entero sin formato regional.
- **Privacidad:** los datos del perro creado los escribe el usuario y no salen del
  dispositivo por acción de la app, que nunca los envía a ningún servidor, ni se
  registran en logs. **Decidido:** se mantiene `android:allowBackup="true"` con
  las reglas actuales, así que Auto Backup de Android copia `dogs.db` a la copia
  de seguridad de Google del usuario y sus perros se restauran al cambiar de
  móvil. Consecuencia a tener presente: en un dispositivo restaurado el catálogo
  ya viene sembrado, de modo que `dogs.json` no se pedirá nunca allí. No se añade
  criterio de aceptación para el ciclo de copia y restauración porque no es
  reproducible de forma fiable en el entorno de validación disponible.

## Criterios de aceptación

<!-- Cada uno se responde sí/no mirando la feature funcionando, sin interpretar.
     Si para saber si está cumplido hace falta discutir, todavía no es un
     criterio: pártelo en dos.

       MAL   - [ ] El login funciona bien
       BIEN  - [ ] Dado un email sin @, cuando presiono Entrar, entonces veo
                   "Email no válido" bajo el campo y no se llama a la API -->

**Seed del catálogo**

- [ ] **AC-01** — Dada una instalación limpia con conexión, cuando abro la app, entonces se descarga `dogs.json` una sola vez, la tabla `dogs` contiene 10 filas con `isUserCreated = false` y la lista muestra esos 10 perros.
- [ ] **AC-02** — Dado el catálogo ya sembrado, cuando cierro y vuelvo a abrir la app con conexión, entonces la lista se pinta desde Room y no se emite ninguna petición a `dogs.json`.
- [ ] **AC-03** — Dada una instalación limpia sin conexión, cuando abro la app, entonces veo un mensaje de error con un botón Reintentar y no hay ningún acceso disponible para crear un perro.
- [ ] **AC-04** — Dado el estado de AC-03, cuando recupero la conexión y pulso Reintentar, entonces aparecen los 10 perros y el acceso a crear queda disponible.
- [ ] **AC-05** — Dado el catálogo ya sembrado, cuando abro la app en modo avión, entonces veo los 10 perros sin ningún mensaje de error.

**Caché progresiva del detalle**

- [ ] **AC-06** — Dado un perro del catálogo cuyo detalle nunca se ha abierto, cuando lo abro con conexión, entonces se pide `details/{id}.json`, veo peso, origen y temperamento, y esos tres valores quedan guardados en su fila de `dogs`.
- [ ] **AC-07** — Dado un detalle ya visitado, cuando lo abro en modo avión, entonces veo los mismos datos completos y no se emite ninguna petición de red.
- [ ] **AC-08** — Dado un perro del catálogo cuyo detalle nunca se ha abierto, cuando lo abro en modo avión, entonces veo un error con Reintentar; y al recuperar la conexión y pulsar Reintentar, veo el detalle completo.

**Alta de un perro**

- [ ] **AC-09** — Dado el catálogo sembrado, cuando pulso el acceso a añadir perro, entonces se abre un formulario con los 7 campos vacíos: nombre, raza, edad, descripción, peso, origen y temperamento.
- [ ] **AC-10** — Dado el formulario con al menos un campo vacío o solo con espacios, cuando pulso Guardar, entonces cada campo inválido muestra su mensaje de error, no se inserta ninguna fila en `dogs` y sigo en el formulario.
- [ ] **AC-11** — Dado el campo edad con un valor fuera de 0–30, cuando pulso Guardar, entonces veo un mensaje de error específico bajo el campo edad y no se guarda nada.
- [ ] **AC-12** — Dados los 7 campos válidos, cuando pulso Guardar, entonces vuelvo a la lista, el perro nuevo aparece en la primera posición con su distintivo textual, la búsqueda queda vacía y la fila se ha insertado con `isUserCreated = true`.
- [ ] **AC-13** — Dado un perro creado, cuando abro su detalle en modo avión, entonces veo sus 7 campos y no se emite ninguna petición de red.
- [ ] **AC-14** — Dado un perro creado, cuando cierro la app, la mato desde el sistema y la vuelvo a abrir, entonces el perro sigue en la lista con los mismos datos.
- [ ] **AC-15** — Dados los 7 campos válidos, cuando pulso Guardar tres veces seguidas lo más rápido posible, entonces se inserta exactamente un perro.

**Ciclo de vida del formulario**

- [ ] **AC-16** — Dado el formulario con algún campo relleno, cuando pulso atrás, entonces aparece una confirmación de descarte; si cancelo, sigo en el formulario con los datos intactos; si confirmo, vuelvo a la lista y no se ha guardado nada.
- [ ] **AC-17** — Dado el formulario vacío, cuando pulso atrás, entonces vuelvo a la lista directamente, sin confirmación.
- [ ] **AC-18** — Dado el formulario relleno y con errores visibles, cuando giro el dispositivo, entonces conservo los valores de los 7 campos y los mensajes de error.
- [ ] **AC-19** — Dado el formulario relleno, cuando el sistema mata el proceso y vuelvo a la app, entonces el formulario reaparece con los valores que había escrito.

**Lista mixta y búsqueda**

- [ ] **AC-20** — Dados perros creados y del catálogo, cuando abro la lista, entonces los creados aparecen antes que los del catálogo, entre ellos del más reciente al más antiguo, y el catálogo conserva su orden de `id` ascendente.
- [ ] **AC-21** — Dado un texto de búsqueda que coincide con un perro creado y con uno del catálogo, cuando lo escribo, entonces la lista muestra ambos; y si escribo un texto sin coincidencias, veo "No hay resultados" conservando lo escrito.
- [ ] **AC-22** — Dado un perro creado, cuando lo veo en la lista y en su detalle, entonces su imagen es el placeholder con la inicial del nombre y el lector de pantalla anuncia tanto la imagen como el distintivo de perro creado.

**Adaptación visual**

- [ ] **AC-23** — Dado el formulario con el teclado abierto y el tamaño de fuente del sistema al máximo, cuando recorro la pantalla, entonces puedo alcanzar los 7 campos y el botón Guardar, sin texto cortado.

## Cómo se demuestra

<!-- Una línea por criterio de arriba: qué evidencia prueba que se cumple.
     Un test con nombre, una captura, un log, una grabación. Al menos uno
     probado en un dispositivo real, no solo en el emulador o simulador. "Debería
     funcionar" no es evidencia. -->

**Toda la evidencia de esta lista está PLANIFICADA. Nada se ha ejecutado
todavía; los resultados se registrarán en `TASKS.md` al validar.**

**Corrección sobre el entorno de validación.** Se dio por hecho que no había
dispositivo físico, pero al ejecutar la validación apareció uno conectado: un
**Pixel 8a con Android 17 (API 37)**. Las comprobaciones manuales se han hecho
sobre él, así que el requisito de la plantilla de probar en un dispositivo real
**sí se cumple**. Lo que quede pendiente no lo estará por falta de dispositivo,
sino por lo anotado en `TASKS.md` (T-17).

- AC-01 → `DogRepositoryImplTest` (JVM, API falsa + DAO falso) comprobando una única llamada y 10 filas insertadas, más captura de la lista en dispositivo tras borrar los datos de la app.
- AC-02 → `DogRepositoryImplTest`: con el DAO ya poblado, el contador de llamadas de la API falsa se queda en 0.
- AC-03 → Comprobación manual en dispositivo en modo avión tras borrar datos: captura del error con Reintentar y sin FAB.
- AC-04 → Continuación de la comprobación anterior: captura de la lista poblada y del FAB visible tras desactivar el modo avión y pulsar Reintentar.
- AC-05 → Comprobación manual en dispositivo: captura de la lista completa en modo avión.
- AC-06 → `DogRepositoryImplTest` verificando la petición y la escritura de `weight`/`origin`/`temperament`, más `DogDaoTest` (androidTest, Room en memoria) sobre la actualización.
- AC-07 → `DogRepositoryImplTest`: segunda lectura sin llamadas a la API falsa, más comprobación manual en modo avión.
- AC-08 → Comprobación manual en dispositivo: captura del error con Reintentar y del detalle tras recuperar la conexión.
- AC-09 → `AddDogScreenTest` (androidTest, Compose) comprobando la presencia de los 7 campos vacíos.
- AC-10 → `AddDogViewModelTest` (JVM) sobre el estado de errores y la ausencia de inserción, más `AddDogScreenTest` sobre los mensajes visibles.
- AC-11 → `AddDogViewModelTest` con los casos 0, 30, 31, -1, vacío y no numérico.
- AC-12 → `DogDaoTest` sobre la inserción y `DogViewModelTest` sobre el orden y la búsqueda limpia; captura en dispositivo de la lista con el perro nuevo arriba.
- AC-13 → Comprobación manual en dispositivo en modo avión: captura del detalle completo del perro creado.
- AC-14 → Comprobación manual en dispositivo: `adb shell am force-stop`, reapertura y captura de la lista.
- AC-15 → `AddDogViewModelTest` invocando el guardado tres veces seguidas y comprobando una sola inserción en el DAO falso.
- AC-16 → `AddDogScreenTest` sobre el diálogo, cancelar y confirmar; grabación en dispositivo del gesto atrás.
- AC-17 → `AddDogScreenTest`: atrás con formulario vacío no muestra diálogo.
- AC-18 → Comprobación manual en dispositivo: rotar con el formulario relleno y capturas antes y después.
- AC-19 → Comprobación manual en dispositivo con "No conservar actividades" activado en opciones de desarrollador, o `adb shell am kill`, con capturas antes y después.
- AC-20 → `DogViewModelTest` sobre el orden de la lista resultante, más captura en dispositivo.
- AC-21 → `DogViewModelTest` sobre el filtrado de la lista combinada y el estado sin resultados.
- AC-22 → `DogScreenTest` comprobando el `contentDescription` del placeholder y el texto del distintivo sobre el árbol de semántica, que es la evidencia principal; más comprobación manual con TalkBack **con TalkBack en el dispositivo**. Si no lo incluye, se dará por verificado por el test y se dirá así.
- AC-23 → Comprobación manual en dispositivo con el tamaño de fuente del sistema al máximo y el teclado abierto: capturas de la parte superior e inferior del formulario.

El dispositivo usado y el resultado de cada comprobación quedan registrados
en `TASKS.md`.

---

<!-- PARA EL AGENTE, ANTES DE DAR LA SPEC POR CERRADA, comprueba:

     1. ¿"Fuera de alcance" tiene algo escrito? Si está vacío, no se decidió.
     2. ¿Cada criterio se responde sí/no sin discutir?
     3. ¿Todo lo de "Cómo encaja" tiene una ruta real del repo detrás?

     Si alguna falla, vuelve a esa sección y pregunta. Si las tres pasan,
     dímelo: la spec está lista para construir en una sesión nueva. -->
