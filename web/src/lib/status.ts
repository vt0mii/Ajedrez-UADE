import type { GameState } from "./types";

// Etiqueta y color del estado de la partida. Lo comparten el panel de la
// partida y el listado de partidas guardadas.
export const STATUS_STYLES: Record<
  GameState,
  { label: string; className: string }
> = {
  IN_PROGRESS: {
    label: "En juego",
    className: "bg-success/10 text-success ring-success/30",
  },
  CHECK: {
    label: "Jaque",
    className: "bg-danger/10 text-danger ring-danger/30",
  },
  CHECKMATE: {
    label: "Jaque mate",
    className: "bg-gold-400/10 text-gold-300 ring-gold-400/30",
  },
  ABANDONED: {
    label: "Abandonada",
    className: "bg-ink-800 text-ink-300 ring-ink-700",
  },
};
