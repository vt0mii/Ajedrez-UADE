"use client";

import { findKing, FILES, isLightSquare, SIZE } from "@/lib/board";
import type { GameView } from "@/lib/types";
import { Square } from "./Square";

type ChessBoardProps = {
  game: GameView;
  selected: string | null;
  legalMoves: string[];
  flipped: boolean;
  disabled: boolean;
  onSquareClick: (square: string) => void;
};

export function ChessBoard({
  game,
  selected,
  legalMoves,
  flipped,
  disabled,
  onSquareClick,
}: ChessBoardProps) {
  const rows = Array.from({ length: SIZE }, (_, index) =>
    flipped ? SIZE - 1 - index : index,
  );
  const columns = Array.from({ length: SIZE }, (_, index) =>
    flipped ? SIZE - 1 - index : index,
  );

  const checkSquare =
    game.status === "CHECK" || game.status === "CHECKMATE"
      ? findKing(game.board, game.turn)
      : null;
  const lastMove = game.lastMove;

  const squares = rows.flatMap((row) =>
    columns.map((column) => ({
      row,
      column,
      square: `${FILES[column]}${SIZE - row}`,
    })),
  );

  return (
    <div className="@container w-full">
      <div className="grid grid-cols-8 overflow-hidden rounded-xl shadow-2xl shadow-black/60 ring-1 ring-black/40 select-none">
        {squares.map(({ row, column, square }) => {
          const piece = game.board[row][column];
          const isLastMove =
            lastMove !== null &&
            (lastMove.from === square || lastMove.to === square);

          return (
            <Square
              key={square}
              square={square}
              piece={piece}
              light={isLightSquare(row, column)}
              selected={selected === square}
              legal={legalMoves.includes(square)}
              capturable={legalMoves.includes(square) && piece !== null}
              lastMove={isLastMove}
              inCheck={checkSquare === square}
              disabled={disabled}
              rankLabel={column === columns[0] ? String(SIZE - row) : undefined}
              fileLabel={row === rows[rows.length - 1] ? FILES[column] : undefined}
              onClick={onSquareClick}
            />
          );
        })}
      </div>
    </div>
  );
}
