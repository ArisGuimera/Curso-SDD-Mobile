# SPEC.md

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



## Fuera de alcance

<!-- Va casi al principio a propósito: es lo que evita que el agente se invente
     trabajo a mitad de camino. Si lo dejas vacío, llenará el vacío por su
     cuenta y te enterarás en la review. -->

- 
- 

## Cómo encaja en el proyecto

<!-- Esto lo saca el agente del repo. Exige rutas reales, no descripciones:
     "sigue las convenciones del proyecto" no sirve de nada. -->

**Dónde vive:**  <!-- módulo, carpeta, paquete -->

**Se apoya en:**  <!-- lo que YA existe y tiene que reutilizar, no duplicar -->

**Sigue el patrón de:**  <!-- una feature parecida ya hecha, con su ruta -->

## Cómo está hecho por dentro

<!-- Las decisiones que, si no las tomas tú, las toma el agente. Y las suyas
     son siempre las más cómodas para él, no para tu proyecto. -->

**Capas que toca:**  <!-- UI, presentación, dominio, datos... y en qué orden fluye -->

**Qué se crea nuevo:**  <!-- clases, tablas, endpoints, pantallas. Con nombre -->

**Qué se modifica:**  <!-- de lo que ya existe. Si hay un modelo compartido, avisa -->

**Contratos:**  <!-- forma de los datos que entran y salen: request, response, esquema -->

**Prohibido:**  <!-- librerías nuevas, atajos entre capas, lo que no debe tocar aunque le tiente -->

## Qué pasa cuando no sale bien

<!-- El camino feliz lo resuelve cualquiera. Lo que vuelve como bug es esto.
     Las últimas tres filas son la vida real de una app: pasan todos los días
     en el bolsillo del usuario. Si alguna fila no aplica de verdad, escribe
     "no aplica" y por qué; no la dejes vacía. -->

| Situación | Qué tiene que pasar |
|---|---|
| No hay datos |  |
| La entrada es inválida |  |
| Falla algo de lo que depende |  |
| Tarda demasiado |  |
| No hay conexión (o se corta a mitad) |  |
| El usuario sale de la app a mitad de camino |  |
| El sistema mata el proceso y el usuario vuelve |  |

## Criterios de aceptación

<!-- Cada uno se responde sí/no mirando la feature funcionando, sin interpretar.
     Si para saber si está cumplido hace falta discutir, todavía no es un
     criterio: pártelo en dos.

       MAL   - [ ] El login funciona bien
       BIEN  - [ ] Dado un email sin @, cuando presiono Entrar, entonces veo
                   "Email no válido" bajo el campo y no se llama a la API -->

- [ ] Dado ___, cuando ___, entonces ___.
- [ ] Dado ___, cuando ___, entonces ___.
- [ ] Dado ___, cuando ___, entonces ___.

## Cómo se demuestra

<!-- Una línea por criterio de arriba: qué evidencia prueba que se cumple.
     Un test con nombre, una captura, un log, una grabación. Al menos uno
     probado en un dispositivo real, no solo en el emulador o simulador. "Debería
     funcionar" no es evidencia. -->

- Criterio 1 → 
- Criterio 2 → 
- Criterio 3 → 

---

<!-- PARA EL AGENTE, ANTES DE DAR LA SPEC POR CERRADA, comprueba:

     1. ¿"Fuera de alcance" tiene algo escrito? Si está vacío, no se decidió.
     2. ¿Cada criterio se responde sí/no sin discutir?
     3. ¿Todo lo de "Cómo encaja" tiene una ruta real del repo detrás?

     Si alguna falla, vuelve a esa sección y pregunta. Si las tres pasan,
     dímelo: la spec está lista para construir en una sesión nueva. -->
