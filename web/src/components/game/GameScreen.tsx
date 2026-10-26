"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { ChessBoard } from "@/components/board/ChessBoard";
import { GameStatus } from "@/components/panels/GameStatus";
import { MoveHistory } from "@/components/panels/MoveHistory";
import { PlayerCard } from "@/components/panels/PlayerCard";
import { ResultBanner } from "@/components/panels/ResultBanner";
import { Toast } from "@/components/ui/Toast";
import { useGame } from "@/hooks/useGame";
import { API_URL } from "@/lib/api";
import { missingPieces, oppositeColor, playerOf } from "@/lib/board";
import type { Color } from "@/lib/types";

export function GameScreen({ gameId }: { gameId: string }) {
  const {
    game,
    loading,
    error,
    selected,
    legalMoves,
    busy,
    notice,
    notify,
    selectSquare,
    clearSelection,
    abandon,
  } = useGame(gameId);

  const [flipped, setFlipped] = useState(false);

  useEffect(() => {
    function onKeyDown(event: KeyboardEvent) {
      if (event.key === "Escape") {
        clearSelection();
      }
      if (event.key.toLowerCase() === "f") {
        setFlipped((value) => !value);
      }
    }

    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [clearSelection]);

  if (loading) {
    return (
      <main className="flex flex-1 items-center justify-center px-4 py-20">
        <div className="flex flex-col items-center gap-3">
          <span className="piece animate-pulse text-4xl text-gold-400">♞</span>
          <p className="text-sm text-ink-400">Cargando partida…</p>
        </div>
      </main>
    );
  }

  if (error || !game) {
    return (
      <main className="mx-auto flex w-full max-w-lg flex-1 flex-col justify-center px-4 py-20 text-center">
        <p className="font-serif text-3xl text-ink-100">
          No pudimos abrir la partida
        </p>
        <p className="mt-3 text-sm text-ink-300">
          {error ?? "Partida no encontrada."}
        </p>

        <div className="mt-6 rounded-xl border border-ink-800 bg-ink-900/60 p-4 text-left">
          <p className="text-xs text-ink-400">
            Verificá que el backend esté corriendo:
          </p>
          <code className="mt-2 block overflow-x-auto font-mono text-xs text-ink-300">
            java -cp bin com.uade.ajedrez.App --web
          </code>
          <p className="mt-2 text-[11px] text-ink-400">API: {API_URL}</p>
        </div>

        <Link
          href="/"
          className="mt-6 inline-flex items-center justify-center rounded-lg bg-gold-400 px-4 py-2.5 text-sm font-medium text-ink-950 transition-colors hover:bg-gold-300"
        >
          Volver al inicio
        </Link>
      </main>
    );
  }

  const bottomColor: Color = flipped ? "BLACK" : "WHITE";
  const topColor = oppositeColor(bottomColor);
  const finished = game.status === "CHECKMATE" || game.status === "ABANDONED";

  async function copyLink() {
    try {
      await navigator.clipboard.writeText(window.location.href);
      notify("Enlace de la partida copiado.", "info");
    } catch {
      notify("El navegador no permitió copiar el enlace.");
    }
  }

  return (
    <main className="mx-auto w-full max-w-[1400px] flex-1 px-4 py-6 sm:px-6 lg:py-8">
      <div className="mb-5 flex flex-wrap items-end justify-between gap-3">
        <div className="min-w-0">
          <Link
            href="/"
            className="text-xs text-ink-400 transition-colors hover:text-ink-100"
          >
            ← Volver al inicio
          </Link>
          <h1 className="mt-1 truncate font-mono text-xs text-ink-400 sm:text-sm">
            {game.id}
          </h1>
        </div>
        <p className="text-[11px] text-ink-400">Sincronizado cada 2 s</p>
      </div>

      <div className="grid gap-5 lg:grid-cols-[250px_minmax(0,1fr)_270px] lg:items-start">
        <aside className="order-2 space-y-3 lg:order-1">
          <PlayerCard
            player={playerOf(game, topColor)}
            active={!finished && game.turn === topColor}
            captured={missingPieces(game.board, oppositeColor(topColor))}
          />
          <MoveHistory history={game.history} />
          <PlayerCard
            player={playerOf(game, bottomColor)}
            active={!finished && game.turn === bottomColor}
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
              selected={selected}
              legalMoves={legalMoves}
              flipped={flipped}
              disabled={finished || busy}
              onSquareClick={selectSquare}
            />
            <ResultBanner game={game} />
          </div>
        </section>

        <aside className="order-3">
          <GameStatus
            game={game}
            busy={busy}
            onFlip={() => setFlipped((value) => !value)}
            onAbandon={abandon}
            onCopyLink={copyLink}
          />
        </aside>
      </div>

      {notice ? <Toast message={notice.message} tone={notice.tone} /> : null}
    </main>
  );
}
