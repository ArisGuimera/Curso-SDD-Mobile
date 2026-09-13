# Mobile Guidelines

Al crear una especificación, tener en cuenta los siguientes puntos y aplicar los que correspondan a la funcionalidad. Si falta definir un comportamiento, consultarlo antes de asumirlo.

- **Ciclo de vida:** contemplar qué ocurre al pasar a segundo plano, volver a la app, recrear una pantalla o terminarse el proceso. Definir qué sucede con las operaciones en curso.
- **Conservación del estado:** considerar qué debe mantenerse al navegar o reabrir la app, como formularios, selecciones y posición de desplazamiento. Distinguir estado temporal de datos persistentes.
- **Conectividad:** contemplar ausencia de conexión, red lenta, desconexiones durante una operación y recuperación. Diferenciar el primer uso de los casos con datos previamente descargados.
- **Persistencia y consistencia:** definir qué se guarda en el dispositivo y cómo convive con los datos remotos. Considerar actualizaciones, duplicados, conflictos y compatibilidad con datos guardados por versiones anteriores.
- **Estados de interfaz:** contemplar carga, contenido, vacío, error y éxito. Dar feedback sobre las acciones y permitir recuperarse de los errores cuando corresponda.
- **Navegación e interrupciones:** considerar volver atrás, cancelar, salir con cambios sin guardar y regresar desde otra app. Contemplar enlaces externos o notificaciones si forman parte del flujo.
- **Interacción y formularios:** tener en cuenta teclado, foco, validaciones y visibilidad de las acciones. Evitar que pulsaciones repetidas produzcan envíos u operaciones duplicadas.
- **Pantallas y adaptación visual:** contemplar tamaños de pantalla, orientaciones admitidas, áreas del sistema y cambios en el tamaño del texto. Evitar contenido cortado o controles inaccesibles.
- **Accesibilidad:** considerar lector de pantalla, etiquetas, orden de foco, contraste y áreas táctiles. No comunicar información únicamente mediante colores o gestos.
- **Permisos y capacidades del dispositivo:** contemplar permisos rechazados o revocados y capacidades no disponibles. Solicitar solo los accesos necesarios y definir alternativas cuando corresponda.
- **Rendimiento y recursos:** mantener la interfaz receptiva durante operaciones de red, almacenamiento y procesamiento. Considerar memoria, batería, uso de datos, imágenes y listas grandes.
- **Trabajo en segundo plano:** cuando aplique, contemplar que una tarea pueda retrasarse o interrumpirse. Definir cómo se reanuda y cómo se evitan efectos duplicados.
- **Privacidad y seguridad:** considerar qué información se almacena, transmite o muestra, cómo se protege y cuándo se elimina. Evitar datos sensibles en logs y mensajes de error.
- **Idiomas y formatos:** cuando aplique, contemplar traducciones, longitud de textos, fechas, zonas horarias, números y unidades.
- **Validación en mobile:** incluir pruebas de los escenarios relevantes, como interrupciones, reapertura, cambios de conectividad, permisos y distintos tamaños de pantalla. Complementar los tests automatizados con comprobaciones en dispositivo, emulador o simulador según lo que se necesite validar.

Estos puntos no amplían automáticamente el alcance. Incorporar al SPEC.md los requisitos, decisiones técnicas y criterios de aceptación que se deriven de los puntos aplicables y de las decisiones confirmadas.
