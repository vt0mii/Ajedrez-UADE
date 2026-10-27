# Frontend — Ajedrez UADE

Interfaz web del trabajo práctico, hecha con **Next.js 16 (App Router)**,
**TypeScript** y **Tailwind CSS v4**.

El frontend no implementa ninguna regla del ajedrez: pinta el `GameView` que
devuelve el adaptador HTTP de Java y le manda las jugadas. La validación, el
jaque y el jaque mate viven en el dominio.

## Requisitos

- Node.js 20.9 o superior.
- El backend Java compilado (`..\build.ps1`).

## Cómo levantarlo

En una terminal, desde la raíz del repositorio:

```powershell
.\build.ps1
java -cp bin com.uade.ajedrez.App --web
```

En otra terminal:

```powershell
cd web
npm install        # solo la primera vez
npm run dev
```

Abrí <http://localhost:3000>.

> En PowerShell puede estar bloqueada la ejecución de `npm.ps1`. Si `npm` falla
> con un error de *execution policy*, usá `cmd /c "npm run dev"`.

La URL del backend se configura con `NEXT_PUBLIC_API_URL` (por defecto
`http://localhost:8080`). Copiá `.env.local.example` a `.env.local` para
cambiarla.

## Scripts

| Comando         | Qué hace                              |
| --------------- | ------------------------------------- |
| `npm run dev`   | Servidor de desarrollo en el puerto 3000 |
| `npm run build` | Build de producción                    |
| `npm start`     | Sirve el build de producción           |
| `npm run lint`  | ESLint                                 |

## Estructura

```
src/
├── app/
│   ├── layout.tsx          # shell: header, footer, tipografías
│   ├── page.tsx            # lobby
│   └── game/[id]/page.tsx  # partida (param en el servidor)
├── components/
│   ├── board/              # ChessBoard, Square, Piece
│   ├── panels/             # PlayerCard, MoveHistory, GameStatus, ResultBanner
│   ├── lobby/              # LobbyScreen
│   ├── game/               # GameScreen
│   └── ui/                 # Button, Input, Toast
├── hooks/
│   └── useGame.ts          # carga, polling y acciones de la partida
└── lib/
    ├── api.ts              # cliente tipado del backend
    ├── board.ts            # helpers de coordenadas y presentación
    └── types.ts            # contrato GameView
```

## Flujo de una jugada

1. Click en una pieza propia → `GET /api/games/{id}/legal-moves?from=e2`.
2. Las casillas devueltas se marcan con un punto.
3. Click en un destino → `POST /api/games/{id}/moves`.
4. La respuesta reemplaza el `GameView` en pantalla.

Si el backend rechaza la jugada, se muestra el mensaje en un toast.

## Atajos

- `F`: girar el tablero.
- `Esc`: deseleccionar la pieza.
- Botón ☾/☀ de la cabecera: alternar entre tema oscuro y claro. La preferencia
  se guarda en una cookie y se mantiene al recargar.

El ID de la partida aparece en el panel derecho, junto al botón para copiar el
enlace.

## Sincronización

El estado se refresca cada 2 segundos mientras la pestaña está visible. Alcanza
para jugar en dos dispositivos (hotseat) sin agregar websockets.
