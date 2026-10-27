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
- `command`: `ICommand` / `MoveCommand` con ejecución y deshacer a nivel de tablero, integrado en las jugadas y el replay.
- `model/ChessReplay`: reproducción independiente desde el tablero inicial estándar, con avance y retroceso.
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
- `adapter/in/http`: `HttpGameServer` expone los casos de uso como API JSON usando el servidor que trae el JDK (sin frameworks); `GameViewJson` y `JsonBody` resuelven el contrato. Se arranca con `--web`.
- `adapter/out/persistence`: `FileGameRepository` (archivos `.properties` en `games/`), `InMemoryGameRepository` (tests/demos) y `GameSerializer`.
- `adapter/out/JavaGameLogger`: implementación de `GameLogger` con `java.util.logging`.

### Composición (`App`)

Único lugar que conoce las implementaciones concretas. El adaptador de entrada se elige por argumento (`--web` o consola); agregar otro adaptador solo requiere tocar esta clase.

## Funcionalidades actuales

- Tablero con disposición inicial y validación mediante estrategias por pieza.
- Alternancia de turnos, detección de jaque y jaque mate.
- Historial de movimientos y consulta de **movimientos legales por casilla** (pensado para el frontend).
- Interfaz de consola con nombres de jugadores, comando `legal <casilla>`, `help` y `quit`.
- Frontend web en `web/` (Next.js + Tailwind): tablero, historial, jaque, jaque mate, abandono, tema claro/oscuro y sincronización cada 2 segundos.
- Persistencia de partidas por id en `games/`, con restauración de turno, estado e historial.
- Ids de partida validados (`[A-Za-z0-9_-]{1,64}`), lo que evita path traversal al cargar archivos.
- Manejo de entradas incorrectas, EOF y errores de persistencia.
- Replay de partidas finalizadas por `CHECKMATE` o `ABANDONED`, tanto recién terminadas como guardadas, con avance y retroceso sin modificar la partida ni los archivos.

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
java -ea -cp bin com.uade.ajedrez.application.service.ReplayTest
java -ea -cp bin com.uade.ajedrez.infrastructure.adapter.out.persistence.GameSerializerTest
java -ea -cp bin com.uade.ajedrez.infrastructure.adapter.in.http.HttpGameServerTest
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

### Modo web

```powershell
java -cp bin com.uade.ajedrez.App --web              # http://localhost:8080
java -cp bin com.uade.ajedrez.App --web --port 9090  # otro puerto
```

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/health` | Estado del servidor |
| `POST` | `/api/games` | Crea una partida (`whitePlayerName`, `blackPlayerName`) |
| `GET` | `/api/games/{id}` | Devuelve el `GameView` actual |
| `POST` | `/api/games/{id}/moves` | Mueve una pieza (`from`, `to`) |
| `GET` | `/api/games/{id}/legal-moves?from=e2` | Destinos legales desde una casilla |
| `POST` | `/api/games/{id}/abandon` | Abandona la partida |

Los errores devuelven `{ "error": "CODIGO", "message": "..." }`: `400` para entradas inválidas, `404` si la partida no existe y `409` si el movimiento viola las reglas. El diseño del frontend está en [`docs/DISENO-FRONT.md`](docs/DISENO-FRONT.md).

Cada partida se guarda automáticamente en `games/<id>.properties` después de cada movimiento.

### Replay

Al finalizar una partida, ingresar `replay` para reproducirla o `replay <id>` para reproducir otra partida guardada. `exit` cierra el programa desde ese menú. También se puede ingresar `replay <id>` en el primer pedido de nombre de jugador, sin crear una partida nueva. Al salir de esa reproducción se vuelve al pedido de nombre.

La reproducción siempre muestra primero el tablero inicial estándar. Los controles son `next` (avanzar una jugada), `prev` (retroceder una jugada) y `exit` (salir del replay). Al llegar a los extremos se mantiene la posición. Una partida abandonada sin movimientos también puede reproducirse.

Solo se admiten partidas en `CHECKMATE` o `ABANDONED`; `IN_PROGRESS` y `CHECK` se rechazan. Se muestra el resultado original separado del estado de la posición que se está viendo: el abandono no agrega un movimiento al historial.

El replay reconstruye comandos desde el historial de coordenadas existente sobre un tablero independiente. No guarda cambios, no altera la partida original y no modifica el formato de persistencia. Cada sesión tiene su propia navegación. No se agregan reglas de tablas, promoción, enroque ni captura al paso.

El frontend se levanta aparte: `cd web`, `npm install` y `npm run dev` (instrucciones completas en [`web/README.md`](web/README.md)).

En IntelliJ IDEA, abrir la carpeta del proyecto, seleccionar **JDK 17** como Project SDK y ejecutar el `main` de `com.uade.ajedrez.App`.

## Excepciones y logging

`ChessGameService` recibe el puerto `GameLogger`; `App` inyecta `JavaGameLogger`, que escribe en la consola/`stderr` mediante `java.util.logging`.

- **INFO**: creación de partida, movimientos válidos, abandono y fin de partida.
- **WARNING**: entradas inválidas y movimientos rechazados.
- **SEVERE**: fallas inesperadas, con causa y traza.

`InvalidMoveInputException` identifica errores de formato o coordenadas. Las jugadas que incumplen las reglas se informan con `MoveResult` y, en la capa de aplicación, con `MoveRejectedException` (que conserva el `Reason` para mapearlo a un futuro código HTTP). `GamePersistenceException` envuelve errores de lectura/escritura o datos guardados inválidos conservando la causa original.

## Próximos pasos

1. Exponer deshacer jugadas durante la partida activa (hoy el retroceso solo existe dentro del replay).
2. Reglas restantes: enroque, promoción, captura al paso y tablas por repetición/50 movimientos.
3. Migrar los tests a JUnit 5 y agregar un build reproducible (Maven o Gradle) cuando el equipo lo decida.
