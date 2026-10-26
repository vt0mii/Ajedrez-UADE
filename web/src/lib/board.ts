import type { Color, GameView, MoveView, PieceType, PieceView } from "./types";

export const SIZE = 8;
export const FILES = ["a", "b", "c", "d", "e", "f", "g", "h"];

// "e2" -> 6 (la fila 0 es el rango 8).
export function toRow(square: string): number {
  return SIZE - Number(square[1]);
}

// "e2" -> 4.
export function toColumn(square: string): number {
  return FILES.indexOf(square[0]);
}

export function toSquare(row: number, column: number): string {
  return `${FILES[column]}${SIZE - row}`;
}

// a8 es clara, así que (fila + columna) par también.
export function isLightSquare(row: number, column: number): boolean {
  return (row + column) % 2 === 0;
}

export function pieceAt(
  board: (PieceView | null)[][],
  square: string,
): PieceView | null {
  return board[toRow(square)]?.[toColumn(square)] ?? null;
}

export function findKing(
  board: (PieceView | null)[][],
  color: Color,
): string | null {
  for (let row = 0; row < SIZE; row++) {
    for (let column = 0; column < SIZE; column++) {
      const piece = board[row][column];
      if (piece?.type === "KING" && piece.color === color) {
        return toSquare(row, column);
      }
    }
  }
  return null;
}

const PIECE_ORDER: PieceType[] = [
  "QUEEN",
  "ROOK",
  "BISHOP",
  "KNIGHT",
  "PAWN",
];

const INITIAL_COUNTS: Record<PieceType, number> = {
  KING: 1,
  QUEEN: 1,
  ROOK: 2,
  BISHOP: 2,
  KNIGHT: 2,
  PAWN: 8,
};

// Piezas de ese color que ya no están: las que capturó el rival. Es solo
// para mostrar, no toca ninguna regla.
export function missingPieces(
  board: (PieceView | null)[][],
  color: Color,
): PieceType[] {
  const remaining = { ...INITIAL_COUNTS };
  for (const row of board) {
    for (const piece of row) {
      if (piece?.color === color) {
        remaining[piece.type] -= 1;
      }
    }
  }

  const missing: PieceType[] = [];
  for (const type of PIECE_ORDER) {
    for (let count = 0; count < remaining[type]; count++) {
      missing.push(type);
    }
  }
  return missing;
}

export type HistoryTurn = {
  number: number;
  white: MoveView;
  black?: MoveView;
};

export function groupMoves(history: MoveView[]): HistoryTurn[] {
  const turns: HistoryTurn[] = [];
  history.forEach((move, index) => {
    const turnIndex = Math.floor(index / 2);
    if (index % 2 === 0) {
      turns.push({ number: turnIndex + 1, white: move });
    } else {
      turns[turnIndex].black = move;
    }
  });
  return turns;
}

export function playerOf(game: GameView, color: Color) {
  return color === "WHITE" ? game.whitePlayer : game.blackPlayer;
}

export function oppositeColor(color: Color): Color {
  return color === "WHITE" ? "BLACK" : "WHITE";
}
