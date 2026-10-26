"use client";

const COOKIE_NAME = "chess-theme";
const ONE_YEAR_IN_SECONDS = 60 * 60 * 24 * 365;

/**
 * Alterna entre tema oscuro y claro escribiendo `data-theme` en <html> y
 * guardando la preferencia en una cookie. El layout la lee en el servidor,
 * así que el próximo render ya sale con el tema correcto.
 */
export function ThemeToggle() {
  function toggleTheme() {
    const root = document.documentElement;
    const next = root.dataset.theme === "light" ? "dark" : "light";
    root.dataset.theme = next;
    document.cookie = `${COOKIE_NAME}=${next}; path=/; max-age=${ONE_YEAR_IN_SECONDS}; samesite=lax`;
  }

  return (
    <button
      type="button"
      onClick={toggleTheme}
      aria-label="Cambiar entre tema claro y oscuro"
      title="Cambiar tema"
      className="flex h-9 w-9 items-center justify-center rounded-full border border-ink-800 text-base text-ink-300 transition-colors hover:border-ink-600 hover:text-ink-100 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-gold-400"
    >
      <span aria-hidden className="theme-icon theme-icon-dark leading-none">
        ☾
      </span>
      <span aria-hidden className="theme-icon theme-icon-light leading-none">
        ☀
      </span>
    </button>
  );
}
