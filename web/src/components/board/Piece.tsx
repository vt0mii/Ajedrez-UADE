import type { PieceType, PieceView } from "@/lib/types";

const GLYPHS: Record<PieceType, string> = {
  KING: "♚",
  QUEEN: "♛",
  ROOK: "♜",
  BISHOP: "♝",
  KNIGHT: "♞",
  PAWN: "♟",
};

export function Piece({
  piece,
  className = "",
}: {
  piece: PieceView;
  className?: string;
}) {
  return (
    <span
      aria-hidden
      className={`piece ${piece.color === "WHITE" ? "piece-white" : "piece-black"} ${className}`}
    >
      {GLYPHS[piece.type]}
    </span>
  );
}
