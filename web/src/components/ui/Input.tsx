import type { InputHTMLAttributes } from "react";

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  id: string;
  label: string;
  hint?: string;
};

export function Input({ id, label, hint, className = "", ...props }: InputProps) {
  return (
    <label htmlFor={id} className="block">
      <span className="mb-1.5 block text-[11px] font-medium tracking-[0.12em] text-ink-400 uppercase">
        {label}
      </span>
      <input
        id={id}
        className={`w-full rounded-lg border border-ink-700 bg-ink-950/60 px-3 py-2.5 text-sm text-ink-100 transition-colors outline-none placeholder:text-ink-600 focus:border-gold-400/70 focus:ring-2 focus:ring-gold-400/15 ${className}`}
        {...props}
      />
      {hint ? (
        <span className="mt-1.5 block text-xs text-ink-400">{hint}</span>
      ) : null}
    </label>
  );
}
