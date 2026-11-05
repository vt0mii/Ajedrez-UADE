"use client";

import { Piece } from "@/components/board/Piece";
import type { Color, PieceType } from "@/lib/types";

const CHOICES: { type: PieceType; label: string }[] = [
  { type: "QUEEN", label: "Reina" },
  { type: "ROOK", label: "Torre" },
  { type: "BISHOP", label: "Alfil" },
  { type: "KNIGHT", label: "Caballo" },
];

type PromotionDialogProps = {
  color: Color;
  onChoose: (piece: PieceType) => void;
  onCancel: () => void;
};

// Se muestra encima del tablero cuando un peon llega a la ultima fila. La
// jugada todavia no se mando: primero se elige la pieza.
export function PromotionDialog({ color, onChoose, onCancel }: PromotionDialogProps) {
  return (
    <div className="absolute inset-0 z-40 flex items-center justify-center rounded-xl bg-ink-950/80 p-4 backdrop-blur-sm">
      <div
        role="dialog"
        aria-modal="true"
        aria-label="Elegir pieza de promoción"
        className="w-full max-w-sm rounded-xl border border-gold-500/40 bg-ink-900 p-4 shadow-2xl shadow-black/60"
      >
        <p className="text-center font-serif text-2xl text-ink-100">Promoción</p>
        <p className="mt-1 text-center text-xs text-ink-400">
          Elegí la pieza para el peón
        </p>

        <div className="mt-4 grid grid-cols-4 gap-2">
          {CHOICES.map(({ type, label }, index) => (
            <button
              key={type}
              type="button"
              autoFocus={index === 0}
              onClick={() => onChoose(type)}
              className="group flex flex-col items-center gap-1 rounded-lg border border-ink-700 bg-ink-850 px-2 py-3 transition-colors hover:border-gold-500/70 hover:bg-ink-800 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-gold-300"
            >
              <Piece piece={{ type, color }} className="text-3xl" />
              <span className="text-[10px] text-ink-400 group-hover:text-ink-100">
                {label}
              </span>
            </button>
          ))}
        </div>

        <button
          type="button"
          onClick={onCancel}
          className="mt-3 w-full rounded-lg px-3 py-2 text-xs text-ink-400 transition-colors hover:text-ink-100"
        >
          Cancelar
        </button>
      </div>
    </div>
  );
}
