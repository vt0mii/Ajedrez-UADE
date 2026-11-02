import Link from "next/link";
import { playerOf } from "@/lib/board";
import { isFinishedStatus } from "@/lib/status";
import type { GameView } from "@/lib/types";

function winnerName(game: GameView): string | null {
  if (game.status === "CHECKMATE" && game.winner) {
    return playerOf(game, game.winner).name;
  }
  if (game.status === "ABANDONED") {
    // El que abandona es el que tenía el turno, así que gana el otro.
    return playerOf(game, game.turn === "WHITE" ? "BLACK" : "WHITE").name;
  }
  return null;
}

function resultTitle(game: GameView): string {
  if (game.status === "CHECKMATE") {
    return "Jaque mate";
  }
  if (game.status === "DRAW") {
    return "Tablas";
  }
  return "Partida abandonada";
}

export function ResultBanner({ game }: { game: GameView }) {
  if (!isFinishedStatus(game.status)) {
    return null;
  }

  const winner = winnerName(game);

  return (
    <div className="result-overlay absolute inset-0 z-30 flex flex-col items-center justify-center gap-3 rounded-xl px-6 text-center backdrop-blur-[2px]">
      <p className="font-serif text-4xl leading-none text-gold-300">
        {resultTitle(game)}
      </p>
      <p className="text-sm text-ink-300">
        {winner ? `Gana ${winner}` : "La partida terminó en tablas"}
      </p>
      <div className="mt-2 flex flex-wrap items-center justify-center gap-2">
        <Link
          href={`/game/${game.id}/replay`}
          className="inline-flex items-center justify-center rounded-lg bg-gold-400 px-4 py-2.5 text-sm font-medium text-ink-950 transition-colors hover:bg-gold-300"
        >
          Ver replay
        </Link>
        <Link
          href="/"
          className="inline-flex items-center justify-center rounded-lg border border-ink-700 px-4 py-2.5 text-sm font-medium text-ink-200 transition-colors hover:border-ink-600 hover:text-ink-100"
        >
          Nueva partida
        </Link>
      </div>
    </div>
  );
}
