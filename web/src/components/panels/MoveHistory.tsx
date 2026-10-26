import { groupMoves } from "@/lib/board";
import type { MoveView } from "@/lib/types";

function formatMove(move: MoveView) {
  return `${move.from}-${move.to}`;
}

export function MoveHistory({ history }: { history: MoveView[] }) {
  const turns = groupMoves(history);

  return (
    <section className="overflow-hidden rounded-xl border border-ink-800 bg-ink-900/60">
      <header className="flex items-center justify-between border-b border-ink-800 px-4 py-2.5">
        <h2 className="text-[11px] font-semibold tracking-[0.12em] text-ink-400 uppercase">
          Historial
        </h2>
        <span className="text-[11px] text-ink-400">
          {history.length} {history.length === 1 ? "jugada" : "jugadas"}
        </span>
      </header>

      {turns.length === 0 ? (
        <p className="px-4 py-6 text-center text-xs text-ink-400">
          Todavía no hay movimientos.
        </p>
      ) : (
        <ol className="scroll-thin max-h-60 overflow-y-auto p-1.5">
          {turns.map((turn) => (
            <li
              key={turn.number}
              className="grid grid-cols-[1.75rem_1fr_1fr] items-center gap-1 rounded-md px-2 py-1 text-sm odd:bg-ink-800/40"
            >
              <span className="text-[11px] text-ink-400">{turn.number}.</span>
              <span className="font-mono text-[13px] text-ink-100">
                {formatMove(turn.white)}
              </span>
              <span className="font-mono text-[13px] text-ink-300">
                {turn.black ? formatMove(turn.black) : ""}
              </span>
            </li>
          ))}
        </ol>
      )}
    </section>
  );
}
