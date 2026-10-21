# Ajedrez UADE

Trabajo práctico de la materia **Ingeniería de Software** de **UADE**.

Desarrollamos un ajedrez en Java aplicando **arquitectura hexagonal (puertos y adaptadores)**. El objetivo es que el núcleo de reglas de negocio sea independiente de la interfaz de usuario, de la persistencia y de cualquier framework, de modo que se puedan agregar nuevos adaptadores (por ejemplo, una API HTTP para un frontend web) sin tocar el dominio.

## Integrantes

- Manuel Quintana Miño
- Lisandro Tortora
- Lautaro Ezequiel Umerez
- Tomas Nahuel Medina Cotos

## Arquitectura

Las dependencias apuntan siempre hacia el núcleo:

```
domain  <-  application  <-  infrastructure
                              ^
                              |
                         App (composition root)
```

### Dominio (`domain`)

No conoce consola, archivos, HTTP ni frameworks.

- `model`: `ChessGame` (agregado raíz), `Board`, `Piece`, `Position`, `Move`, `Turn`, `Player`, `GameId`, `GameState`, `MoveResult`.
- `strategy`: una estrategia de movimiento por tipo de pieza (`StratPawn`, `StratKnight`, ...).
- `service`: `MovementService` valida movimientos, jaque, jaque mate y ahogado.
- `command`: `ICommand` / `MoveCommand` con ejecución y deshacer a nivel de tablero.
- `port/out`: `GameLogger`, el contrato de logging que implementa la infraestructura.
- `exception`: `InvalidMoveInputException`, `GamePersistenceException`.

### Aplicación (`application`)

Orquesta casos de uso y traduce entre el dominio y el exterior.

- `port/in`: `CreateGameUseCase`, `GetGameUseCase`, `MakeMoveUseCase`, `LegalMovesUseCase`, `AbandonGameUseCase` (y `ChessGameUseCases` como agregado de conveniencia).
- `port/out`: `GameRepositoryPort`.
- `dto`: `NewGameCommand`, `MoveCommand`, `GameView` con el tablero, turno, estado, historial y movimientos legales.
- `service`: `ChessGameService` implementa los casos de uso y `GameViewMapper` arma el modelo de lectura.
- `exception`: errores de aplicación (`GameNotFoundException`, `MoveRejectedException`, `InvalidCommandException`).

### Infraestructura (`infrastructure`)

- `adapter/in/console`: `ConsoleView` traduce texto a comandos y `ConsoleRenderer` dibuja el `GameView`. El loop de juego vive acá, no en el dominio.
- `adapter/out/persistence`: `FileGameRepository` (archivos `.properties` en `games/`), `InMemoryGameRepository` (tests/demos) y `GameSerializer`.
- `adapter/out/JavaGameLogger`: implementación de `GameLogger` con `java.util.logging`.

### Composición (`App`)

Único lugar que conoce las implementaciones concretas. Para agregar el adaptador web en la próxima fase solo se modifica esta clase.

## Funcionalidades actuales

- Tablero con disposición inicial y validación mediante estrategias por pieza.
- Alternancia de turnos, detección de jaque, jaque mate y **ahogado (tablas)**.
- Historial de movimientos y consulta de **movimientos legales por casilla** (pensado para el frontend).
- Interfaz de consola con nombres de jugadores, comando `legal <casilla>`, `help` y `quit`.
- Persistencia de partidas por id en `games/`, con restauración de turno, estado e historial.
- Ids de partida validados (`[A-Za-z0-9_-]{1,64}`), lo que evita path traversal al cargar archivos.
- Manejo de entradas incorrectas, EOF y errores de persistencia.
- Comando de movimiento con ejecución y deshacer a nivel de tablero; su integración en el flujo de la partida queda pendiente.

## Requisitos

- **JDK 17** o superior, con `java` y `javac` en el `PATH`.
- **PowerShell** para ejecutar el script de compilación.
- Opcional: **IntelliJ IDEA** con el SDK del proyecto configurado.

El proyecto no requiere dependencias externas ni utiliza Maven o Gradle.

## Compilación y tests

El script limpia `bin/`, compila el código principal y los tests, y ejecuta la suite con aserciones habilitadas:

```powershell
.\build.ps1
```

Para volver a ejecutar todos los tests:

```powershell
java -ea -cp bin com.uade.ajedrez.AllTests
```

Tests individuales:

```powershell
java -ea -cp bin com.uade.ajedrez.domain.model.BoardTest
java -ea -cp bin com.uade.ajedrez.domain.service.MovementServiceTest
java -ea -cp bin com.uade.ajedrez.domain.model.ChessGameTest
java -ea -cp bin com.uade.ajedrez.domain.command.MoveCommandTest
java -ea -cp bin com.uade.ajedrez.application.service.ChessGameServiceTest
java -ea -cp bin com.uade.ajedrez.infrastructure.adapter.out.persistence.GameSerializerTest
java -ea -cp bin com.uade.ajedrez.ErrorHandlingTest
```

Los tests usan aserciones de Java; es necesario incluir `-ea`.

## Ejecución

Después de compilar:

```powershell
java -cp bin com.uade.ajedrez.App
```

Al iniciar se piden los nombres de los jugadores (Enter usa `White` y `Black`). Luego se ingresa origen y destino separados por espacios, por ejemplo `e2 e4`.

Comandos disponibles:

- `e2 e4`: realizar un movimiento.
- `legal e2`: listar los destinos legales de la pieza en `e2`.
- `help`: mostrar la ayuda.
- `quit`: abandonar la partida.

Cada partida se guarda automáticamente en `games/<id>.properties` después de cada movimiento.

En IntelliJ IDEA, abrir la carpeta del proyecto, seleccionar **JDK 17** como Project SDK y ejecutar el `main` de `com.uade.ajedrez.App`.

## Excepciones y logging

`ChessGameService` recibe el puerto `GameLogger`; `App` inyecta `JavaGameLogger`, que escribe en la consola/`stderr` mediante `java.util.logging`.

- **INFO**: creación de partida, movimientos válidos, abandono y fin de partida.
- **WARNING**: entradas inválidas y movimientos rechazados.
- **SEVERE**: fallas inesperadas, con causa y traza.

`InvalidMoveInputException` identifica errores de formato o coordenadas. Las jugadas que incumplen las reglas se informan con `MoveResult` y, en la capa de aplicación, con `MoveRejectedException` (que conserva el `Reason` para mapearlo a un futuro código HTTP). `GamePersistenceException` envuelve errores de lectura/escritura o datos guardados inválidos conservando la causa original.

## Próximos pasos

1. **Adaptador web** (`adapter/in/web`): servidor HTTP, JSON y endpoints `/api/games`, `/api/games/{id}`, `/api/games/{id}/moves` y `/api/games/{id}/legal-moves`. El `GameView` ya contiene todo lo que el frontend necesita.
2. Integrar `MoveCommand` en `ChessGame` para exponer deshacer desde los casos de uso.
3. Reglas restantes: enroque, promoción, captura al paso y tablas por repetición/50 movimientos.
4. Migrar los tests a JUnit 5 y agregar un build reproducible (Maven o Gradle) cuando el equipo lo decida.
