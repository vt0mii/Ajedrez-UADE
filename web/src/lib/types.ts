// Contrato de la API. Es el mismo GameView que devuelve Java.

export type Color = "WHITE" | "BLACK";

export type PieceType = "PAWN" | "KNIGHT" | "BISHOP" | "ROOK" | "QUEEN" | "KING";

export type GameState = "IN_PROGRESS" | "CHECK" | "CHECKMATE" | "ABANDONED";

export type PieceView = {
  type: PieceType;
  color: Color;
};

export type PlayerView = {
  name: string;
  color: Color;
};

export type MoveView = {
  from: string;
  to: string;
};

export type GameView = {
  id: string;
  status: GameState;
  turn: Color;
  turnNumber: number;
  whitePlayer: PlayerView;
  blackPlayer: PlayerView;
  // 8x8. board[0][0] es a8 y board[7][7] es h1.
  board: (PieceView | null)[][];
  history: MoveView[];
  lastMove: MoveView | null;
  winner: Color | null;
};
