# Frontend — Ajedrez UADE

Interfaz web del trabajo práctico, hecha con **Next.js 16 (App Router)**,
**TypeScript** y **Tailwind CSS v4**.

El frontend no implementa ninguna regla del ajedrez: pinta el `GameView` que
devuelve el adaptador HTTP de Java y le manda las jugadas. La validación, el
jaque, el jaque mate y las tablas por ahogado viven en el dominio.

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

| Comando              | Qué hace                              |
| -------------------- | ------------------------------------- |
| `npm run dev`        | Servidor de desarrollo en el puerto 3000 |
| `npm run build`      | Build de producción                    |
| `npm start`          | Sirve el build de producción           |
| `npm run lint`       | ESLint                                 |
| `npm run export:pdf` | Genera los PDFs de la documentación    |

## Estructura

```
src/
├── app/
│   ├── layout.tsx          # shell: header, footer, tipografías
│   ├── page.tsx            # lobby
│   ├── docs/page.tsx       # índice de documentación
│   ├── docs/[slug]/page.tsx # documento markdown
│   ├── docs/[slug]/imprimir/page.tsx # versión imprimible
│   ├── docs/[slug]/pdf/route.ts # descarga del PDF del documento
│   ├── docs/diagramas/page.tsx # todos los diagramas, a ancho completo
│   ├── docs/diagramas/pdf/route.ts # descarga del PDF de diagramas
│   ├── game/[id]/page.tsx  # partida (param en el servidor)
│   └── game/[id]/replay/page.tsx # replay de una partida terminada
├── components/
│   ├── board/              # ChessBoard, Square, Piece
│   ├── docs/               # DocsScreen, DiagramsScreen, DocMarkdown, MermaidDiagram
│   ├── panels/             # PlayerCard, MoveHistory, GameStatus, ResultBanner
│   ├── lobby/              # LobbyScreen
│   ├── game/               # GameScreen, ReplayScreen
│   └── ui/                 # Button, Input, Toast
├── hooks/
│   └── useGame.ts          # carga, polling y acciones de la partida
└── lib/
    ├── api.ts              # cliente tipado del backend
    ├── board.ts            # helpers de coordenadas y presentación
    ├── docs.ts             # lista, lectura y links de los documentos
    ├── markdown.ts         # slugs y extracción de títulos
    ├── status.ts           # etiquetas y colores del estado de la partida
    └── types.ts            # contrato GameView y GameSummary
scripts/
└── export-pdf.mjs          # genera los PDFs de docs/export con Edge
```

## Flujo de una jugada

1. Click en una pieza propia → `GET /api/games/{id}/legal-moves?from=e2`.
2. Las casillas devueltas se marcan con un punto.
3. Click en un destino → `POST /api/games/{id}/moves`.
4. La respuesta reemplaza el `GameView` en pantalla.

Si el backend rechaza la jugada, se muestra el mensaje en un toast.

## Partidas guardadas

El lobby pide `GET /api/games` y muestra las partidas de `games/` ordenadas por
la última modificación. Cada fila trae jugadores, estado, turno, cantidad de
jugadas, última jugada y fecha; al hacer clic se abre la partida.

El backend ya guarda después de cada movimiento. El botón **Guardar partida**
del panel derecho llama a `POST /api/games/{id}/save` y confirma con un toast
cuando el archivo quedó actualizado.

## Replay

Las partidas terminadas por **jaque mate**, **abandono** o **tablas** se pueden
reproducir. El overlay de fin de partida tiene **Ver replay** y el lobby muestra
un enlace **Replay** en esas filas.

La pantalla `/game/[id]/replay` pide cada posición con
`GET /api/games/{id}/replay?position=N`. Se navega con los botones **Inicio**,
**Anterior**, **Siguiente** y **Final**, o con las flechas ← →, `Inicio` y `Fin`.
El frontend no reconstruye jugadas: solo muestra el tablero que devuelve el
backend para cada posición.

## Documentación

`/docs` lee los markdown del repositorio (`README.md`, `web/README.md` y
`docs/*.md`) y los muestra con un menú de documentos y un índice lateral. La
lista está fija en `lib/docs.ts`; los enlaces entre documentos se traducen a
rutas de la app y el render usa `react-markdown` con `remark-gfm`.

La pantalla es un Server Component: lee los archivos con `node:fs` en cada
request, así que no hay que copiar los documentos ni reconstruir el HTML a mano.

Los diagramas Mermaid se dibujan en `/docs/diagramas`, a ancho completo, y desde
cada documento hay tarjetas que llevan al diagrama correspondiente.

## Exportar a PDF

`npm run export:pdf` abre las versiones imprimibles con Edge en modo headless y
deja cuatro archivos en `docs/export/`: `ARQUITECTURA.pdf`, `JUSTIFICACION.pdf`,
`DISENO-FRONT.pdf` y `DIAGRAMAS-DE-FLUJO.pdf`. Los tres documentos salen en A4
vertical; los diagramas, en A4 apaisado con una hoja por diagrama. Todo se
exporta en tema oscuro.

Antes de correrlo tiene que estar levantado el frontend (`npm run dev` o
`npm start`) en `http://localhost:3000`. Se puede cambiar con `DOCS_BASE_URL`.
El script usa Microsoft Edge; si está en otra ruta, se indica con
`PUPPETEER_EXECUTABLE_PATH`.

Los PDFs se sirven desde `/docs/{slug}/pdf` y `/docs/diagramas/pdf`, así que la
web siempre entrega el último archivo generado.

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
