"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { playerOf } from "@/lib/board";
import type { GameState, GameView } from "@/lib/types";

const STATUS_STYLES: Record<GameState, { label: string; className: string }> = {
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

type GameStatusProps = {
  game: GameView;
  busy: boolean;
  onFlip: () => void;
  onAbandon: () => void;
  onCopyLink: () => void;
};

export function GameStatus({
  game,
  busy,
  onFlip,
  onAbandon,
  onCopyLink,
}: GameStatusProps) {
  const [confirming, setConfirming] = useState(false);
  const status = STATUS_STYLES[game.status];
  const turnPlayer = playerOf(game, game.turn);
  const finished = game.status === "CHECKMATE" || game.status === "ABANDONED";

  return (
    <section className="space-y-4 rounded-xl border border-ink-800 bg-ink-900/60 p-4">
      <div className="flex items-center justify-between gap-2">
        <span
          className={`rounded-full px-2.5 py-1 text-[10px] font-semibold tracking-[0.12em] uppercase ring-1 ring-inset ${status.className}`}
        >
          {status.label}
        </span>
        <span className="text-xs text-ink-400">Turno {game.turnNumber}</span>
      </div>

      <div>
        <p className="text-[11px] tracking-[0.12em] text-ink-400 uppercase">
          {finished ? "Partida finalizada" : "Juegan"}
        </p>
        <p className="mt-0.5 text-lg font-medium text-ink-100">
          {finished ? "—" : turnPlayer.name}
        </p>
        {!finished ? (
          <p className="text-xs text-ink-400">
            {game.turn === "WHITE" ? "Blancas" : "Negras"}
          </p>
        ) : null}
      </div>

      <div className="border-t border-ink-800 pt-4">
        <div className="flex items-center justify-between gap-2">
          <p className="text-[11px] font-medium tracking-[0.12em] text-ink-400 uppercase">
            ID de partida
          </p>
          <button
            type="button"
            onClick={onCopyLink}
            className="text-[11px] font-medium text-gold-300 transition-colors hover:text-gold-400"
          >
            Copiar enlace
          </button>
        </div>
        <code className="mt-1.5 block font-mono text-[11px] leading-relaxed break-all text-ink-300">
          {game.id}
        </code>
      </div>

      <div className="space-y-2 border-t border-ink-800 pt-4">
        <Button variant="ghost" className="w-full" onClick={onFlip}>
          Girar tablero
        </Button>

        {!finished && !confirming ? (
          <Button
            variant="danger"
            className="w-full"
            disabled={busy}
            onClick={() => setConfirming(true)}
          >
            Abandonar
          </Button>
        ) : null}

        {!finished && confirming ? (
          <div className="space-y-2 rounded-lg border border-danger/40 bg-danger/5 p-3">
            <p className="text-xs text-ink-300">¿Abandonar la partida?</p>
            <div className="flex gap-2">
              <Button
                variant="danger"
                className="flex-1"
                disabled={busy}
                onClick={() => {
                  setConfirming(false);
                  onAbandon();
                }}
              >
                Sí
              </Button>
              <Button
                variant="ghost"
                className="flex-1"
                onClick={() => setConfirming(false)}
              >
                No
              </Button>
            </div>
          </div>
        ) : null}
      </div>
    </section>
  );
}
