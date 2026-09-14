# Curso Spec-Driven Mobile Development (SDMD)

Aprende a construir apps Android **especificando antes de programar**. En lugar de pedirle features sueltas a una IA y rezar, este curso te enseña un flujo donde **la especificación es la fuente de verdad** y el agente implementa contra criterios de aceptación verificables.

<p align="center">
<a href="https://youtube.com/live/tnSrleO_tL8?feature=share"><img src="img/thumbnail_sdmd.webp" style="height: 75%; width:75%;"/></a></p>

<p align="center"><i>▶ <a href="https://youtube.com/live/tnSrleO_tL8?feature=share">SDD Mobile: crea mejores apps con IA</a></i></p>

<p align="center">
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white" alt="Kotlin">
<img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
<img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
<img src="https://img.shields.io/badge/SDD-Spec%20Driven-%2300E58E?style=for-the-badge" alt="Spec Driven Development">
</p>

<p align="center">
<a href="https://youtube.com/live/tnSrleO_tL8?feature=share">
<img src="https://img.shields.io/badge/▶%20VER%20EL%20CURSO-FF0000?style=for-the-badge&logo=youtube&logoColor=white" alt="Ver el curso en YouTube">
</a>
</p>

----------

## 🎯 Qué es el Spec-Driven Development

El problema de programar con IA no es que escriba mal el código: es que **rellena los huecos que tú no has definido**, y siempre con la opción más cómoda para ella. Si no decides tú qué pasa sin conexión, qué se guarda, qué es obligatorio o qué ocurre al girar la pantalla, lo decidirá el agente y te enterarás en la review.

SDMD le pone un proceso delante:

| Etapa | Documento | Qué resuelve |
| :--: | --- | --- |
| **1️⃣** | **SPEC.md** | Qué construimos, qué queda fuera, reglas de negocio, casos de error y **criterios de aceptación verificables**. Sin código. |
| **2️⃣** | **PLAN.md** | Cómo se construye: capas, contratos, dependencias, estado y estrategia de validación. Sin implementar. |
| **3️⃣** | **TASKS.md** | Tareas pequeñas y ordenadas, con dependencias y comprobaciones, ligadas a los criterios que resuelven. |
| **4️⃣** | Implementación | Solo lo que está en el alcance, demostrando cada criterio con evidencia real. |

> [!IMPORTANT]
> Que un documento esté **completo** no significa que esté **aprobado**. Ninguna etapa avanza sola: cada una se revisa contigo. Y "debería funcionar" no es evidencia.

----------

## 📋 Plantillas del curso

El corazón del repo. Copia estas plantillas a tu proyecto y pide al agente que las complete **contigo**:

| Documento | Para qué sirve | Enlace |
| --- | --- | :--: |
| 📝 **SPEC_TEMPLATE.md** | Plantilla de especificación: alcance, flujos, reglas, comportamiento mobile y criterios de aceptación | [**Abrir**](docs/SPEC_TEMPLATE.md) |
| 🧩 **PLAN_TEMPLATE.md** | Plantilla de plan técnico: componentes, contratos, datos, errores, dependencias y validación | [**Abrir**](docs/PLAN_TEMPLATE.md) |
| 📱 **MOBILE_GUIDELINES.md** | El checklist que convierte una spec genérica en una spec **de móvil**: ciclo de vida, estado, conectividad, permisos, accesibilidad… | [**Abrir**](docs/MOBILE_GUIDELINES.md) |
| ⚙️ **GENERIC_RULES.md** | Reglas de trabajo del agente: entender antes de cambiar, no inventar, validar de verdad | [**Abrir**](docs/GENERIC_RULES.md) |
| 💬 **PROMPTS.md** | El prompt inicial del curso, listo para copiar y pegar | [**Abrir**](docs/PROMPTS.md) |
| 🤖 **AGENTS.md** | Instrucciones del proyecto para el agente: arquitectura, convenciones y flujo SDMD | [**Abrir**](AGENTS.md) |

> [!NOTE]
> No hay plantilla de `TASKS.md` a propósito: **se deriva del plan aprobado**, porque las tareas dependen de las decisiones técnicas que tome el `PLAN.md`.

### Cómo organizar tus features

```
docs/
├── SPEC_TEMPLATE.md          # plantillas compartidas (no las edites por feature)
├── PLAN_TEMPLATE.md
├── MOBILE_GUIDELINES.md
├── GENERIC_RULES.md
├── PROMPTS.md
└── features/
    └── <nombre-de-la-feature>/
        ├── SPEC.md           # copia de SPEC_TEMPLATE.md, completada contigo
        ├── PLAN.md           # copia de PLAN_TEMPLATE.md, tras aprobar la spec
        └── TASKS.md          # derivado del plan
```

`AGENTS.md` y `CLAUDE.md` ya apuntan a estos documentos, así que el agente los encuentra solo.

----------

## 🐶 El proyecto base

Para practicar SDMD necesitas una app real, no un "hola mundo". Este repo parte de una app Android que **lista perros y muestra su detalle**, consumiendo un JSON estático alojado en GitHub.

Está deliberadamente incompleta: **no tiene persistencia, ni alta de datos, ni funciona sin conexión**. Ahí es donde entras tú con la primera spec del curso.

### Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Clean Architecture** con flujo de datos unidireccional
- **Hilt** para inyección de dependencias
- **Retrofit** + **kotlinx.serialization**
- **Coil 3** para imágenes
- **Navigation 3** con rutas `@Serializable` type-safe

### Estructura

```
app/src/main/java/com/aristidevs/cursopremiumandroid/
├── core/
│   ├── di/              # módulos Hilt: Json, Retrofit, API, repositorio
│   └── navigation/      # Routes.kt (NavKey) + AppNavigation.kt (NavDisplay)
├── data/
│   ├── api/             # DogApiServices + DTOs @Serializable
│   ├── mapper/          # DogMapper: respuesta -> dominio
│   └── DogRepositoryImpl.kt
├── domain/
│   ├── model/           # modelos de dominio planos
│   ├── DogRepository.kt # contrato, propiedad del dominio
│   └── usecase/         # GetDogsUseCase, GetDogDetailUseCase
├── presentation/
│   ├── list/            # DogScreen + DogViewModel (StateFlow<UiState>)
│   └── detail/          # DetailScreen + DogDetailViewModel
└── ui/theme/            # Color.kt, Theme.kt, Type.kt
```

**Las reglas de dependencia son innegociables**: `presentation -> domain` y `data -> domain`. El dominio no conoce Android, ni Retrofit, ni los DTO.

----------

## 🛠 Instalación

### Requisitos

- **Android Studio** (versión compatible con AGP 9.x)
- **JDK 11+**
- **Kotlin 2.2.10** · **compileSdk 37** · **minSdk 26**
- Un agente de IA con acceso al repo (Claude Code, Gemini CLI, Codex…)

### Puesta en marcha

1. Clona el repositorio:

    ```bash
    git clone https://github.com/ArisGuimera/Curso-SDD-Mobile.git
    ```

2. Ábrelo en Android Studio y sincroniza Gradle.
3. Ejecuta la app para comprobar que el catálogo de perros carga.

### Comandos

```bash
./gradlew :app:assembleDebug              # compilar
./gradlew :app:testDebugUnitTest          # tests de JVM
./gradlew :app:lintDebug                  # lint
./gradlew :app:connectedDebugAndroidTest  # tests instrumentados (requiere dispositivo)
```

----------

## 🚦 Cómo seguir el curso

1. **Lee las reglas.** [`AGENTS.md`](AGENTS.md) y [`docs/GENERIC_RULES.md`](docs/GENERIC_RULES.md) son lo que mantiene al agente dentro del carril.
2. **Lanza el prompt inicial.** Copia [`docs/PROMPTS.md`](docs/PROMPTS.md) en tu agente.
3. **Escribe la SPEC contigo, no por ti.** El agente investiga el repo y te pregunta lo que no puede deducir. Responde pocas preguntas por vez y **marca como PENDIENTE lo que no esté decidido**.
4. **Aprueba explícitamente** antes de pasar al plan. Y otra vez antes de implementar.
5. **Exige evidencia.** Un test con nombre no es un test ejecutado. Una captura no demuestra persistencia.

> [!TIP]
> La primera feature del curso —**persistencia con Room y alta de perros**— toca a la vez ciclo de vida, estado sin guardar, conectividad y convivencia entre datos remotos y locales. Es pequeña en pantalla y enorme en decisiones: justo lo que hace falta para que SDMD se note.

----------

## 🤝 Contribuir

Si quieres apoyar mi trabajo puedes hacerlo a través de los siguientes medios:

- Dale a FAV al proyecto (Star)
- Comparte el curso para que llegue a más gente
- Sígueme en mis [redes sociales](https://aristi.dev)

¡Toda ayuda es bienvenida y me permite seguir creando contenido y proyectos open source!

----------

## 👨‍💻 Autor

Desarrollado por **AristiDevs**.

- [YouTube](https://www.youtube.com/@ArisGuimera)
- [Twitter](https://twitter.com/ArisGuimera)
- [LinkedIn](https://www.linkedin.com/in/arisguimera/)

----------

## 🚀 AppCademy.dev

<p align="center">
<a href="https://appcademy.dev"><img src="img/appcademy.webp" style="height: 35%; width:35%;"/></a></p>

Este curso está patrocinado por [AppCademy.dev](https://appcademy.dev) mi plataforma de cursos premium donde no solo aprendemos tecnologías sino que profundizamos en sus desarrollos a través de buenas prácticas y contenido avanzado.

----------

## 📦 Otros Proyectos

Si te gustó este proyecto, no olvides echar un vistazo a otros repositorios:

<table>
<tr>
<td width="50%">
<h3 align="center">Curso Android Básico</h3>
<div align="center">
<a href="https://github.com/ArisGuimera/Android-Expert" target="_blank"><img src="https://i.imgur.com/Jji0CIE.jpg" width="400" alt="Curso básico android"></a>
<p>
<a href="https://github.com/ArisGuimera/Android-Expert" target="_blank">
<img src="https://img.shields.io/badge/CÓDIGO-ff9?style=for-the-badge&logo=github&logoColor=black">
</a>
<a href="https://youtu.be/vJapzH_46a8" target="_blank">
<img src="https://img.shields.io/badge/-Youtube-green?style=for-the-badge&color=fbfc40">
</a>
</p>
<p>Aprende a programar aplicaciones <strong>Android con Kotlin desde cero</strong> - En este curso aprenderás todo lo necesario ya que no es necesario ningún conocimiento previo. Curso <strong>GRATUITO de 12 horas</strong> con todo el código disponible para descargar.</p>
</div>
</td>

<td width="50%">
<h3 align="center">Curso Android Intermedio</h3>
<div align="center">
<a href="https://github.com/ArisGuimera/Android-Expert-Intermedio" target="_blank"><img src="https://i.imgur.com/V48W0sU.jpg" width="400" alt="Curso intermedio Android"></a>
<p>
<a href="https://github.com/ArisGuimera/Android-Expert-Intermedio" target="_blank">
<img src="https://img.shields.io/badge/CÓDIGO-ff9?style=for-the-badge&logo=github&logoColor=black">
</a>
<a href="https://youtu.be/UaR7GSNACsM" target="_blank">
<img src="https://img.shields.io/badge/-Youtube-green?style=for-the-badge&color=fbfc40">
</a>
</p>
<p>Aprende a programar aplicaciones <strong>Android con Kotlin nivel intermedio</strong> - En este curso nos centraremos en las <strong>buenas prácticas, arquitectura y testing</strong>. Curso <strong>GRATUITO de 8 horas</strong> con todo el código disponible para descargar.</p>
</div>
</td>
</tr>
</table>

<table>
<tr>
<td width="50%">
<h3 align="center">Arquitectura MVVM</h3>
<div align="center">
<a href="https://github.com/ArisGuimera/SimpleAndroidMVVM" target="_blank"><img src="https://i.imgur.com/7uCBigG.jpg" width="400" alt="Curso arquitectura MVVM"></a>
<p>
<a href="https://github.com/ArisGuimera/SimpleAndroidMVVM" target="_blank">
<img src="https://img.shields.io/badge/C%C3%93DIGO-80ffaa?style=for-the-badge&logo=github&logoColor=black">
</a>
<a href="https://youtu.be/hhhSMXi0R3E" target="_blank">
<img src="https://img.shields.io/badge/-Youtube-green?style=for-the-badge&color=3fFD7f">
</a>
</p>
<p>Las arquitecturas son <strong>IMPRESCINDIBLES</strong> para poder trabajar como desarrollador/a Android. En este curso, dividido por ramas, irás aprendiendo a implementar una arquitectura real y robusta con inyección de dependencias, clean architecture, testing y mucho más.</p>
</div>
</td>

<td width="50%">
<h3 align="center">Curso Kotlin Multiplatform - Windows 95</h3>
<div align="center">
<a href="https://github.com/ArisGuimera/Windows95KMP" target="_blank"><img src="https://raw.githubusercontent.com/ArisGuimera/Windows95KMP/master/img/kmp_thumbnail.webp" width="400" alt="Curso Kotlin Multiplatform Windows 95"></a>
<p>
<a href="https://github.com/ArisGuimera/Windows95KMP" target="_blank">
<img src="https://img.shields.io/badge/C%C3%93DIGO-cfaae0?style=for-the-badge&logo=github&logoColor=black">
</a>
<a href="https://youtu.be/QFPTUwFW9p8" target="_blank">
<img src="https://img.shields.io/badge/-Youtube-green?style=for-the-badge&color=ff00f4">
</a>
</p>
<p>Replica <strong>Windows 95 desde cero</strong> con Kotlin Multiplatform y Compose Desktop. Un curso <strong>GRATUITO</strong> donde la nostalgia se junta con las prácticas modernas de desarrollo multiplataforma.</p>
</div>
</td>
</tr>
</table>
<br>
