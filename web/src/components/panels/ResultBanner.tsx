import Link from "next/link";
import { playerOf } from "@/lib/board";
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

export function ResultBanner({ game }: { game: GameView }) {
  if (game.status !== "CHECKMATE" && game.status !== "ABANDONED") {
    return null;
  }

  const winner = winnerName(game);
  const checkmate = game.status === "CHECKMATE";

  return (
    <div className="result-overlay absolute inset-0 z-30 flex flex-col items-center justify-center gap-3 rounded-xl px-6 text-center backdrop-blur-[2px]">
      <p className="font-serif text-4xl leading-none text-gold-300">
        {checkmate ? "Jaque mate" : "Partida abandonada"}
      </p>
      <p className="text-sm text-ink-300">
        {winner ? `Gana ${winner}` : "La partida terminó"}
      </p>
      <Link
        href="/"
        className="mt-2 inline-flex items-center justify-center rounded-lg bg-gold-400 px-4 py-2.5 text-sm font-medium text-ink-950 transition-colors hover:bg-gold-300"
      >
        Nueva partida
      </Link>
    </div>
  );
}
