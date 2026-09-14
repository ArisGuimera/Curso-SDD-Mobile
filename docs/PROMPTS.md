Quiero que me ayudes a escribir la especificación de una funcionalidad para esta app: incorporar persistencia con Room y permitir agregar perros. Actualmente la app consulta un JSON de perros alojado en GitHub.

Primero, revisa el proyecto para entender cómo funciona y qué convenciones sigue. También revisa el fichero `MOBILE_GUIDELINES.md` e incorpora al proceso SDMD las revisiones que sean pertinentes.

Después, prepara un borrador de la especificación con la información que podamos comprobar, para completarlo y corregirlo juntos.

La especificación debe definir:

- Objetivo y comportamiento esperado.
- Qué está incluido y qué queda fuera del alcance.
- Flujos, reglas de negocio y casos de error.
- Criterios de aceptación concretos y cómo validar cada uno.
- Restricciones técnicas y decisiones pendientes.

No asumas decisiones que no estén definidas: pregúntame antes de incorporarlas. Puedes proponer opciones y recomendar una, pero espera mi confirmación antes de reflejarla como decisión tomada.

Investiga todo lo que puedas comprobar en el código. Todo lo que no esté resuelto o no pueda deducirse con certeza debe quedar marcado como pendiente.

Hazme pocas preguntas por vez y actualiza la especificación con mis respuestas. Presta especial atención a:

- El funcionamiento sin conexión.
- La persistencia local con Room.
- La convivencia entre los perros obtenidos del JSON y los agregados por el usuario.
- La sincronización y actualización de los datos remotos, si aplica.

No implementes nada hasta que revisemos y aprobemos explícitamente la especificación.