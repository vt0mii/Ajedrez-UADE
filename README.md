# Ajedrez UADE

Trabajo práctico de la materia **Ingeniería de Software** de **UADE**.

Desarrollamos un ajedrez en Java aplicando **arquitectura hexagonal (puertos y adaptadores)**. El objetivo es construir una solución escalable y extensible cuyo núcleo de reglas de negocio sea independiente de interfaces de usuario, mecanismos de persistencia y frameworks externos.

## Integrantes

- Manuel Quintana Miño
- Lisandro Tortora
- Lautaro Ezequiel Umerez
- Tomas Nahuel Medina Cotos

## Arquitectura

El diseño busca que las dependencias apunten hacia el núcleo. Los adaptadores implementan contratos definidos por el dominio, para poder incorporar otras interfaces o formas de almacenamiento sin cambiar las reglas del juego.

- **Dominio (`domain`)**: tablero, piezas, jugadores, turnos, estados y validación de movimientos. Incluye estrategias por tipo de pieza, comandos y excepciones.
- **Puertos (`domain/port`)**: contratos `Input`, `Output` y `GameLogger` para interactuar con el exterior sin depender de implementaciones concretas.
- **Adaptadores (`infrastructure/adapter`)**: `ConsoleView` implementa la entrada y salida por consola; `JavaGameLogger` implementa el registro de eventos con `java.util.logging`.
- **Composición (`App`)**: crea los objetos y conecta el núcleo con los adaptadores.

La independencia del núcleo es el objetivo del diseño. Actualmente, `GameSerializer` está en `domain/util` y realiza operaciones con archivos; su traslado a un adaptador de persistencia detrás de un puerto es una mejora pendiente.

## Funcionalidades actuales

- Tablero con disposición inicial de piezas.
- Validación de movimientos mediante estrategias por tipo de pieza.
- Alternancia de turnos y detección de jaque y jaque mate.
- Interfaz de consola para jugar.
- Comando de movimiento con ejecución y deshacer a nivel de tablero; su integración en el flujo de la partida está pendiente.
- Manejo de entradas incorrectas y registro de eventos y errores.
- Utilidad de persistencia en archivos de propiedades, todavía sin integración con la interfaz de consola.

## Requisitos

- **JDK 17**, con `java` y `javac` disponibles en el `PATH`.
- **PowerShell** para ejecutar el script de compilación.
- Opcional: **IntelliJ IDEA** con el SDK del proyecto configurado en JDK 17.

El proyecto no requiere dependencias externas ni utiliza Maven o Gradle. Ejecutar los siguientes comandos desde la carpeta raíz del repositorio.

## Compilación y tests

El script compila el código principal y los tests en `bin/`, y ejecuta la suite con las aserciones habilitadas:

```powershell
.\build.ps1
```

Para volver a ejecutar todos los tests después de compilar:

```powershell
java -ea -cp bin com.uade.ajedrez.AllTests
```

También se pueden ejecutar individualmente:

```powershell
java -ea -cp bin com.uade.ajedrez.domain.command.MoveCommandTest
java -ea -cp bin com.uade.ajedrez.domain.model.BoardTest
java -ea -cp bin com.uade.ajedrez.domain.service.MovementServiceTest
java -ea -cp bin com.uade.ajedrez.domain.model.ChessManagerTest
java -ea -cp bin com.uade.ajedrez.ErrorHandlingTest
```

Los tests usan aserciones de Java; es necesario incluir `-ea` para verificar los resultados.

## Ejecución

Después de compilar:

```powershell
java -cp bin com.uade.ajedrez.App
```

Ingresar la casilla de origen y la de destino separadas por espacios, por ejemplo `e2 e4`. Para abandonar la partida, ingresar `quit`.

En IntelliJ IDEA, abrir la carpeta del proyecto, seleccionar **JDK 17** como Project SDK y ejecutar el método `main` de `com.uade.ajedrez.App`. Si el IDE no reconoce las fuentes, importar el módulo `Ajedrez-UADE.iml`.

## Excepciones y logging

El dominio utiliza el puerto `GameLogger`. `App` inyecta `JavaGameLogger`, que escribe los logs en la consola de IntelliJ o en la salida de error estándar (`stderr`) al ejecutar desde una terminal.

- **INFO**: inicio, movimientos realizados y finalización de la partida.
- **WARNING**: entradas incorrectas y movimientos rechazados.
- **SEVERE**: errores inesperados, incluyendo su causa y traza.

`InvalidMoveInputException` identifica errores de formato o coordenadas y permite informarlos sin interrumpir la partida. Las jugadas que incumplen las reglas de ajedrez se comunican mediante `MoveResult`. El cierre de la entrada termina normalmente el juego.

`GamePersistenceException` identifica errores de lectura, escritura o datos guardados inválidos y conserva la causa original. Los errores inesperados que llegan a `App` se registran, se informa al usuario y la aplicación termina con código de salida 1.

El constructor de `ChessManager` sin un logger explícito utiliza una implementación silenciosa, útil para pruebas y compatibilidad.
