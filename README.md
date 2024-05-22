# PlayPlanner
Basado en la plantilla: 

## Dependencies

- JDK 17
- Maven 3.8.8
- Docker

## Uso de la aplicación

La aplicación PlayPlanner nos permite:
- Registro, inicio sesión / cierre sesión.
- Crear partidos, pudiendo ver de antemano la disponibilidad de cada pista en el dia que escogamos.
- Unirnos a partidos ya creados. 
- Hablar por chat con los integrantes de un partido al que perteneces.
- Filtrar partidos según varios parámetros para encontrar el partido que más se ajuste a tus preferencias.
- Valorar / reportar al resto de jugadores del partido.
- Ver un listado con todos los partidos disponibles.
- Ver un icono en la barra de navegación con el número de mensajes que has recibido y no has leido, y si se hace clic en el icono, un listado con los chats en los que se han enviado esos mensajes y cuantos han sido en cada chat.

Si eres administrador, podrás:
- Ver los reportes que se hayan hecho, marcarlos como procesados, y acceder al chat del partido pudiendo interactuar en él.
- Retirar el acceso a la plataforma a usuarios.
- En general, acceder a cualquier página de partido e interactuar con el chat.
- Ver un icono en la barra de navegación con el número de reportes que se han hecho y no has leido, y si se hace clic en el icono, un listado con los partidos en los que se han enviado esos reportes.
- El administrador podrá filtrar partidos y ver los partidos disponibles.

Podemos usar la instrucción mvn spring-boot:run desde línea de comandos para arrancar la aplicación.

En el archivo /src/main/resources/import.sql podemos encontrar el script .sql que la aplicación usa para introducir datos de prueba en la BBDD. Entre estos datos de prueba se introducen los siguientes usuarios con las contraseñas que indicamos:
-Usuario: 'a', Contraseña:'aa', ADMINISTRADOR.
-Usuario: 'b', Contraseña:'aa'.
-Usuario: 'c', Contraseña:'aa'.
-Usuario: 'd', Contraseña:'aa'.

## Funcionalidades por desarrollar / terminar

- Terminar el proceso de finalización de partido.
- Permitir que se reporte / valore únicamente después de que el partido haya sido finalizado.
- Permitir que el administrador pueda finalizar un partido si el creador no lo hace.