import { Piece } from "@/components/board/Piece";
import type { Color, PieceType, PlayerView } from "@/lib/types";

type PlayerCardProps = {
  player: PlayerView;
  active: boolean;
  captured: PieceType[];
};

export function PlayerCard({ player, active, captured }: PlayerCardProps) {
  const capturedColor: Color = player.color === "WHITE" ? "BLACK" : "WHITE";

  return (
    <div
      className={`rounded-xl border px-4 py-3 transition-colors ${
        active
          ? "border-gold-400/60 bg-gold-400/[0.06]"
          : "border-ink-800 bg-ink-900/60"
      }`}
    >
      <div className="flex items-center justify-between gap-3">
        <div className="flex items-center gap-2.5">
          <span
            aria-hidden
            className={`h-2.5 w-2.5 shrink-0 rounded-full ${
              player.color === "WHITE"
                ? "bg-[#fbf8f1] ring-1 ring-[#a99a85]"
                : "bg-[#17120d] ring-1 ring-[#4a4034]"
            }`}
          />
          <div className="min-w-0">
            <p className="truncate text-sm font-medium text-ink-100">
              {player.name}
            </p>
            <p className="text-[11px] tracking-wide text-ink-400 uppercase">
              {player.color === "WHITE" ? "Blancas" : "Negras"}
            </p>
          </div>
        </div>

        {active ? (
          <span className="shrink-0 rounded-full bg-gold-400/15 px-2 py-0.5 text-[10px] font-semibold tracking-[0.1em] text-gold-300 uppercase">
            Turno
          </span>
        ) : null}
      </div>

      <div className="mt-2 flex min-h-5 flex-wrap items-center gap-0.5">
        {captured.map((type, index) => (
          <Piece
            key={`${type}-${index}`}
            piece={{ type, color: capturedColor }}
            className="piece-captured text-lg"
          />
        ))}
      </div>
    </div>
  );
}
