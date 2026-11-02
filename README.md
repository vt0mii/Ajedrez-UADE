# Ajedrez

Trabajo práctico de **Ingeniería de Software** de **UADE**.

Es un ajedrez hecho en Java con arquitectura hexagonal (puertos y adaptadores). La idea es que las reglas del juego queden en un núcleo que no sabe nada de la consola, los archivos, HTTP ni los frameworks. Alrededor de ese núcleo hay adaptadores; por eso el mismo motor se puede jugar desde la terminal o desde la web.

## Integrantes

- Manuel Quintana Miño
- Lisandro Tortora
- Lautaro Ezequiel Umerez
- Tomas Nahuel Medina Cotos

## Arquitectura

La explicación completa, capa por capa, está en
[`docs/ARQUITECTURA.md`](docs/ARQUITECTURA.md). Las decisiones de diseño y los
diagramas de clases están en [`docs/JUSTIFICACION.md`](docs/JUSTIFICACION.md).

Las dependencias siempre apuntan hacia el núcleo:

```
domain  <-  application  <-  infrastructure
                              ^
                              |
                         App (composition root)
```

### Dominio (`domain`)

Acá están las reglas y nada más: no hay consola, archivos, HTTP ni frameworks.

- `model`: `ChessGame` (el agregado raíz), `Board`, `Piece`, `Position`, `Move`, `Turn`, `Player`, `GameId`, `GameState` y `MoveResult`.
- `strategy`: una estrategia de movimiento por tipo de pieza (`StratPawn`, `StratKnight`, ...).
- `service`: `MovementService` se encarga de validar jugadas, jaque, jaque mate y ahogado.
- `command`: `ICommand` / `MoveCommand` permiten ejecutar y deshacer una jugada sobre el tablero; lo usan tanto la partida como el replay.
- `model/ChessReplay`: reproduce el historial desde el tablero inicial y puede avanzar o retroceder.
- `port/out`: `GameLogger`, el contrato de logging que implementa la infraestructura.
- `exception`: `InvalidMoveInputException` y `GamePersistenceException`.

### Aplicación (`application`)

Orquesta los casos de uso y traduce entre el dominio y el exterior.

- `port/in`: `CreateGameUseCase`, `GetGameUseCase`, `MakeMoveUseCase`, `LegalMovesUseCase`, `AbandonGameUseCase`, `ReplayGameUseCase`, `ListGamesUseCase` y `SaveGameUseCase`. `ChessGameUseCases` los agrupa para que los adaptadores dependan de una sola interfaz.
- `port/out`: `GameRepositoryPort` y `SavedGame`.
- `dto`: `NewGameCommand`, `MoveCommand`, `GameView` (tablero, turno, estado, historial y movimientos legales) y `GameSummaryView` para el listado.
- `service`: `ChessGameService` implementa los casos de uso y `GameViewMapper` arma el modelo de lectura.
- `exception`: `GameNotFoundException`, `MoveRejectedException` e `InvalidCommandException`.

### Infraestructura (`infrastructure`)

- `adapter/in/console`: `ConsoleView` traduce el texto a comandos y `ConsoleRenderer` dibuja el `GameView`. El loop del juego está acá, no en el dominio.
- `adapter/in/http`: `HttpGameServer` publica los casos de uso como API JSON con el servidor que trae el JDK, sin frameworks. `GameViewJson` y `JsonBody` se ocupan del contrato. Se arranca con `--web`.
- `adapter/out/persistence`: `FileGameRepository` (archivos `.properties` en `games/`), `InMemoryGameRepository` (tests y demos) y `GameSerializer`.
- `adapter/out/JavaGameLogger`: implementación de `GameLogger` con `java.util.logging`.

### Composición (`App`)

Es el único lugar que conoce las implementaciones concretas. Según el argumento (`--web` o consola) elige el adaptador de entrada. Si mañana aparece otro adaptador, solo se toca esta clase.

## Qué se puede hacer hoy

- Jugar con todas las piezas; cada una tiene su propia validación de movimientos.
- Alternar turnos y detectar jaque, jaque mate y tablas por ahogado.
- Ver el historial y consultar los movimientos legales de una casilla (lo usa la web).
- Jugar desde la consola con nombres de jugadores y los comandos `legal <casilla>`, `help` y `quit`.
- Jugar desde la web (`web/`, Next.js + Tailwind): tablero, historial, jaque, jaque mate, tablas, abandono, guardado manual, listado de partidas en el lobby, tema claro/oscuro y sincronización cada 2 segundos.
- Leer la arquitectura, las decisiones y las guías desde `/docs`, con los markdown del repositorio renderizados en la web.
- Guardar cada partida por id en `games/` y retomarla después con el turno, el estado y el historial.
- Revisar partidas terminadas por jaque mate, abandono o tablas, tanto en consola como en la web, sin tocar la partida original.
- Ids de partida validados (`[A-Za-z0-9_-]{1,64}`), así un id no puede salirse de `games/`.
- Manejo de entradas incorrectas, EOF y errores de persistencia.

## Requisitos

- **JDK 17** o superior, con `java` y `javac` en el `PATH`.
- **PowerShell** para correr el script de compilación.
- Opcional: **IntelliJ IDEA** con el SDK del proyecto configurado.

No hay dependencias externas, ni Maven, ni Gradle.

## Compilar y correr los tests

El script limpia `bin/`, compila el código principal y los tests, y ejecuta todo con las aserciones activadas:

```powershell
.\build.ps1
```

Para volver a correr la suite completa:

```powershell
java -ea -cp bin com.uade.ajedrez.AllTests
```

También podés ejecutar un test suelto:

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

Los tests usan `assert` de Java, así que hace falta el `-ea`.

## Cómo jugar

### Consola

Después de compilar:

```powershell
java -cp bin com.uade.ajedrez.App
```

Al arrancar te pide los nombres de los jugadores (Enter usa `White` y `Black`). Después escribís origen y destino separados por un espacio, por ejemplo `e2 e4`.

Comandos:

- `e2 e4`: hacer una jugada.
- `legal e2`: ver los destinos legales de la pieza que está en `e2`.
- `help`: mostrar la ayuda.
- `quit`: abandonar la partida.

### Web

```powershell
java -cp bin com.uade.ajedrez.App --web              # http://localhost:8080
java -cp bin com.uade.ajedrez.App --web --port 9090  # otro puerto
```

El backend solo expone la API. La interfaz se abre en <http://localhost:3000> cuando corre el frontend con `npm run dev`.

La web también tiene una sección `/docs` que lee los `.md` del repositorio y los muestra con su índice. No hay contenido duplicado: si se edita un documento, se ve el cambio al recargar.

### API

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/health` | Estado del servidor |
| `GET` | `/api/games` | Lista las partidas guardadas con un resumen |
| `POST` | `/api/games` | Crea una partida (`whitePlayerName`, `blackPlayerName`) |
| `GET` | `/api/games/{id}` | Devuelve el `GameView` actual |
| `POST` | `/api/games/{id}/moves` | Mueve una pieza (`from`, `to`) |
| `GET` | `/api/games/{id}/legal-moves?from=e2` | Destinos legales desde una casilla |
| `POST` | `/api/games/{id}/abandon` | Abandona la partida |
| `POST` | `/api/games/{id}/save` | Vuelve a guardar la partida |
| `GET` | `/api/games/{id}/replay?position=N` | Devuelve la posición `N` del replay de una partida terminada |

Los errores vienen como `{ "error": "CODIGO", "message": "..." }`: `400` para entradas inválidas, `404` si la partida no existe y `409` cuando la jugada rompe las reglas. El diseño del frontend está en [`docs/DISENO-FRONT.md`](docs/DISENO-FRONT.md).

Cada partida se guarda sola en `games/<id>.properties` después de cada movimiento. El lobby lista esas partidas y las abre con un clic; el botón **Guardar** de la pantalla de juego vuelve a escribir el archivo cuando hace falta.

### Replay

Cuando termina una partida podés escribir `replay` para verla de nuevo, o `replay <id>` para abrir otra partida guardada. `exit` cierra el programa desde ese menú. También podés escribir `replay <id>` en el primer pedido de nombre, sin crear una partida nueva; al salir volvés a ese pedido.

El replay arranca siempre desde el tablero inicial. En consola se maneja con `next` (avanzar), `prev` (retroceder) y `exit` (salir). Si llegás a un extremo, la posición se queda ahí. Una partida abandonada sin movimientos también se puede reproducir.

Solo se pueden reproducir partidas terminadas (`CHECKMATE`, `ABANDONED` o `DRAW`). `IN_PROGRESS` y `CHECK` se rechazan. El resultado original se muestra aparte del estado de la posición que estás viendo: ni el abandono ni las tablas agregan una jugada al historial.

El replay arma las jugadas de nuevo sobre un tablero propio a partir del historial de coordenadas. No guarda cambios, no modifica la partida original ni el formato de los archivos. Cada sesión navega por su cuenta.

En la web, el replay se abre desde el cartel de fin de partida o desde el enlace **Replay** del lobby. La pantalla `/game/{id}/replay` le pide cada posición al backend con `GET /api/games/{id}/replay?position=N` y se maneja con los botones o con las flechas ← →, Inicio y Fin. El endpoint no guarda sesión en el servidor: solo devuelve el `GameView` de la posición pedida. Todavía no hay promoción, enroque, captura al paso ni tablas por repetición o material insuficiente.

El frontend se levanta aparte: `cd web`, `npm install` y `npm run dev`. Las instrucciones completas están en [`web/README.md`](web/README.md).

En IntelliJ IDEA, abrí la carpeta del proyecto, elegí **JDK 17** como Project SDK y ejecutá el `main` de `com.uade.ajedrez.App`.

## Excepciones y logging

`ChessGameService` recibe el puerto `GameLogger`; `App` le pasa `JavaGameLogger`, que escribe en la consola y en `stderr` con `java.util.logging`.

- **INFO**: creación de partida, jugadas válidas, abandono y fin de partida.
- **WARNING**: entradas inválidas y jugadas rechazadas.
- **SEVERE**: fallas inesperadas, con causa y traza.

`InvalidMoveInputException` marca errores de formato o coordenadas. Las jugadas que rompen las reglas se informan con `MoveResult` y, en la capa de aplicación, con `MoveRejectedException` (que conserva el `Reason` para mapearlo a un código HTTP). `GamePersistenceException` envuelve errores de lectura, escritura o datos guardados inválidos, y conserva la causa original.

## Lo que sigue

1. Poder deshacer jugadas durante una partida activa (hoy el retroceso solo existe en el replay).
2. Sumar las reglas que faltan: enroque, promoción, captura al paso y tablas por repetición o por 50 movimientos.
3. Migrar los tests a JUnit 5 sin cambiar el build con `build.ps1`.
