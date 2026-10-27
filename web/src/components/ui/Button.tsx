import type { ButtonHTMLAttributes } from "react";

type Variant = "primary" | "ghost" | "danger";

const VARIANTS: Record<Variant, string> = {
  primary:
    "bg-gold-400 text-ink-950 hover:bg-gold-300 focus-visible:outline-gold-300",
  ghost:
    "border border-ink-700 bg-ink-900/40 text-ink-100 hover:border-ink-600 hover:bg-ink-800 focus-visible:outline-ink-400",
  danger:
    "border border-danger/40 bg-danger/5 text-danger hover:bg-danger/15 focus-visible:outline-danger",
};

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: Variant;
};

export function Button({
  variant = "primary",
  className = "",
  type = "button",
  ...props
}: ButtonProps) {
  return (
    <button
      type={type}
      className={`inline-flex items-center justify-center gap-2 rounded-lg px-4 py-2.5 text-sm font-medium transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 disabled:cursor-not-allowed disabled:opacity-45 ${VARIANTS[variant]} ${className}`}
      {...props}
    />
  );
}
