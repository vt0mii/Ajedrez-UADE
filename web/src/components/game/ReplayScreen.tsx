"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import { ChessBoard } from "@/components/board/ChessBoard";
import { MoveHistory } from "@/components/panels/MoveHistory";
import { PlayerCard } from "@/components/panels/PlayerCard";
import { Button } from "@/components/ui/Button";
import { api, ApiError } from "@/lib/api";
import { missingPieces, oppositeColor, playerOf } from "@/lib/board";
import { STATUS_STYLES } from "@/lib/status";
import type { Color, ReplayView } from "@/lib/types";

function messageOf(error: unknown) {
  return error instanceof ApiError ? error.message : "Error inesperado";
}

// Pantalla de replay. Pide al backend el tablero de cada posición: acá no se
// reconstruye ninguna jugada, solo se navega el historial ya guardado.
export function ReplayScreen({ gameId }: { gameId: string }) {
  const [replay, setReplay] = useState<ReplayView | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [moving, setMoving] = useState(false);

  useEffect(() => {
    let active = true;

    api
      .replay(gameId, 0)
      .then((view) => {
        if (active) setReplay(view);
      })
      .catch((cause) => {
        if (active) setError(messageOf(cause));
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [gameId]);

  const goTo = useCallback(
    async (position: number) => {
      if (!replay || moving) {
        return;
      }

      const target = Math.max(0, Math.min(position, replay.totalMoves));
      if (target === replay.position) {
        return;
      }

      setMoving(true);
      try {
        setReplay(await api.replay(gameId, target));
        setError(null);
      } catch (cause) {
        setError(messageOf(cause));
      } finally {
        setMoving(false);
      }
    },
    [gameId, moving, replay],
  );

  // Atajos de teclado, iguales a los del reproductor de siempre.
  useEffect(() => {
    function onKeyDown(event: KeyboardEvent) {
      if (!replay) {
        return;
      }
      if (event.key === "ArrowLeft") {
        event.preventDefault();
        void goTo(replay.position - 1);
      }
      if (event.key === "ArrowRight") {
        event.preventDefault();
        void goTo(replay.position + 1);
      }
      if (event.key === "Home") {
        event.preventDefault();
        void goTo(0);
      }
      if (event.key === "End") {
        event.preventDefault();
        void goTo(replay.totalMoves);
      }
    }

    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [goTo, replay]);

  if (loading) {
    return (
      <main className="flex flex-1 items-center justify-center px-4 py-20">
        <div className="flex flex-col items-center gap-3">
          <span className="piece animate-pulse text-4xl text-gold-400">♞</span>
          <p className="text-sm text-ink-400">Cargando replay…</p>
        </div>
      </main>
    );
  }

  if (error || !replay) {
    return (
      <main className="mx-auto flex w-full max-w-lg flex-1 flex-col justify-center px-4 py-20 text-center">
        <p className="font-serif text-3xl text-ink-100">
          No pudimos abrir el replay
        </p>
        <p className="mt-3 text-sm text-ink-300">
          {error ?? "Partida no encontrada."}
        </p>
        <p className="mt-2 text-xs text-ink-400">
          El replay solo está disponible para partidas terminadas por jaque
          mate, abandono o tablas.
        </p>
        <div className="mt-6 flex flex-wrap justify-center gap-2">
          <Link
            href={`/game/${gameId}`}
            className="inline-flex items-center justify-center rounded-lg bg-gold-400 px-4 py-2.5 text-sm font-medium text-ink-950 transition-colors hover:bg-gold-300"
          >
            Volver a la partida
          </Link>
          <Link
            href="/"
            className="inline-flex items-center justify-center rounded-lg border border-ink-700 px-4 py-2.5 text-sm font-medium text-ink-200 transition-colors hover:border-ink-600 hover:text-ink-100"
          >
            Inicio
          </Link>
        </div>
      </main>
    );
  }

  const game = replay.game;
  const status = STATUS_STYLES[game.status];
  const result = STATUS_STYLES[replay.result];
  const bottomColor: Color = "WHITE";
  const topColor = oppositeColor(bottomColor);
  const atStart = replay.position === 0;
  const atEnd = replay.position === replay.totalMoves;

  return (
    <main className="mx-auto w-full max-w-[1400px] flex-1 px-4 py-6 sm:px-6 lg:py-8">
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <Link
          href={`/game/${gameId}`}
          className="text-xs text-ink-400 transition-colors hover:text-ink-100"
        >
          ← Volver a la partida
        </Link>
        <p className="text-[11px] text-ink-400">
          Replay · jugada {replay.position} de {replay.totalMoves}
        </p>
      </div>

      <div className="grid gap-5 lg:grid-cols-[250px_minmax(0,1fr)_270px] lg:items-start">
        <aside className="order-2 space-y-3 lg:order-1">
          <PlayerCard
            player={playerOf(game, topColor)}
            active={false}
            captured={missingPieces(game.board, oppositeColor(topColor))}
          />
          <MoveHistory history={game.history} />
          <PlayerCard
            player={playerOf(game, bottomColor)}
            active={false}
            captured={missingPieces(game.board, oppositeColor(bottomColor))}
          />
        </aside>

        <section className="order-1 lg:order-2">
          <div
            className="relative mx-auto w-full"
            style={{ maxWidth: "min(66vh, 600px)" }}
          >
            <ChessBoard
              game={game}
              selected={null}
              legalMoves={[]}
              flipped={false}
              disabled
              onSquareClick={() => {}}
            />
          </div>
        </section>

        <aside className="order-3 space-y-4">
          <section className="space-y-4 rounded-xl border border-ink-800 bg-ink-900/60 p-4">
            <div className="flex items-center justify-between gap-2">
              <span
                className={`rounded-full px-2.5 py-1 text-[10px] font-semibold tracking-[0.12em] uppercase ring-1 ring-inset ${status.className}`}
              >
                {status.label}
              </span>
              <span className="text-xs text-ink-400">
                {replay.position}/{replay.totalMoves}
              </span>
            </div>

            <div>
              <p className="text-[11px] tracking-[0.12em] text-ink-400 uppercase">
                Resultado final
              </p>
              <p className="mt-0.5 text-lg font-medium text-ink-100">
                {result.label}
              </p>
            </div>

            <div className="grid grid-cols-2 gap-2 border-t border-ink-800 pt-4">
              <Button
                variant="ghost"
                className="w-full"
                disabled={moving || atStart}
                onClick={() => void goTo(0)}
              >
                ⏮ Inicio
              </Button>
              <Button
                variant="ghost"
                className="w-full"
                disabled={moving || atStart}
                onClick={() => void goTo(replay.position - 1)}
              >
                ← Anterior
              </Button>
              <Button
                variant="ghost"
                className="w-full"
                disabled={moving || atEnd}
                onClick={() => void goTo(replay.position + 1)}
              >
                Siguiente →
              </Button>
              <Button
                variant="ghost"
                className="w-full"
                disabled={moving || atEnd}
                onClick={() => void goTo(replay.totalMoves)}
              >
                Final ⏭
              </Button>
            </div>

            <p className="text-[11px] leading-relaxed text-ink-400">
              También podés moverte con las flechas ← → y saltar con Inicio y
              Fin.
            </p>
          </section>

          <Link
            href="/"
            className="block text-center text-xs text-ink-400 transition-colors hover:text-ink-100"
          >
            Volver al inicio
          </Link>
        </aside>
      </div>
    </main>
  );
}
