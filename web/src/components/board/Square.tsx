"use client";

import type { PieceView } from "@/lib/types";
import { Piece } from "./Piece";

const PIECE_NAMES: Record<PieceView["type"], string> = {
  KING: "rey",
  QUEEN: "reina",
  ROOK: "torre",
  BISHOP: "alfil",
  KNIGHT: "caballo",
  PAWN: "peón",
};

type SquareProps = {
  square: string;
  piece: PieceView | null;
  light: boolean;
  selected: boolean;
  legal: boolean;
  capturable: boolean;
  lastMove: boolean;
  inCheck: boolean;
  disabled: boolean;
  rankLabel?: string;
  fileLabel?: string;
  onClick: (square: string) => void;
};

export function Square({
  square,
  piece,
  light,
  selected,
  legal,
  capturable,
  lastMove,
  inCheck,
  disabled,
  rankLabel,
  fileLabel,
  onClick,
}: SquareProps) {
  const base = light ? "bg-board-light" : "bg-board-dark";
  const coordinate = light ? "text-board-dark/80" : "text-board-light/80";
  const label = piece
    ? `${PIECE_NAMES[piece.type]} ${piece.color === "WHITE" ? "blanco" : "negro"} en ${square}`
    : `Casilla ${square}`;

  return (
    <button
      type="button"
      aria-label={label}
      disabled={disabled}
      onClick={() => onClick(square)}
      className={`relative flex aspect-square items-center justify-center ${base} ${
        disabled ? "cursor-default" : "cursor-pointer"
      }`}
    >
      {lastMove ? (
        <span className="pointer-events-none absolute inset-0 bg-gold-400/30" />
      ) : null}

      {inCheck ? (
        <span className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle,rgba(229,72,77,0.85),rgba(229,72,77,0)_72%)]" />
      ) : null}

      {selected ? (
        <span className="pointer-events-none absolute inset-0 ring-2 ring-gold-400 ring-inset" />
      ) : null}

      {piece ? <Piece piece={piece} className="relative z-10 text-[10cqw]" /> : null}

      {legal && !capturable ? (
        <span className="pointer-events-none absolute z-20 h-[26%] w-[26%] rounded-full bg-ink-950/25" />
      ) : null}

      {legal && capturable ? (
        <span className="pointer-events-none absolute inset-[4%] z-20 rounded-full border-4 border-ink-950/30" />
      ) : null}

      {rankLabel ? (
        <span
          className={`pointer-events-none absolute top-[3%] left-[6%] text-[1.5cqw] leading-none font-semibold ${coordinate}`}
        >
          {rankLabel}
        </span>
      ) : null}

      {fileLabel ? (
        <span
          className={`pointer-events-none absolute right-[6%] bottom-[3%] text-[1.5cqw] leading-none font-semibold ${coordinate}`}
        >
          {fileLabel}
        </span>
      ) : null}
    </button>
  );
}
