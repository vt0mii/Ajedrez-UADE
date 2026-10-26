type Tone = "error" | "info";

const TONES: Record<Tone, { border: string; icon: string; glyph: string }> = {
  error: { border: "border-danger/40", icon: "text-danger", glyph: "!" },
  info: { border: "border-ink-700", icon: "text-gold-400", glyph: "✓" },
};

export function Toast({ message, tone = "error" }: { message: string; tone?: Tone }) {
  const style = TONES[tone];

  return (
    <div
      role="status"
      className={`fixed bottom-6 left-1/2 z-50 w-[min(92vw,26rem)] -translate-x-1/2 rounded-xl border bg-ink-900/95 px-4 py-3 text-sm text-ink-100 shadow-2xl shadow-black/60 backdrop-blur ${style.border}`}
    >
      <div className="flex items-start gap-2.5">
        <span className={`mt-0.5 font-semibold ${style.icon}`}>{style.glyph}</span>
        <p className="leading-snug">{message}</p>
      </div>
    </div>
  );
}
