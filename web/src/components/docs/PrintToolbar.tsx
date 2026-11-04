"use client";

import Link from "next/link";

// Barra de la versión imprimible. No sale en el PDF: solo sirve en pantalla
// para volver al documento o abrir el diálogo de impresión del navegador.
export function PrintToolbar({
  backHref,
  backLabel = "← Volver al documento",
  downloadHref,
}: {
  backHref: string;
  backLabel?: string;
  downloadHref?: string;
}) {
  return (
    <div className="no-print flex flex-wrap items-center justify-between gap-2 rounded-lg border border-ink-800 bg-ink-900/60 px-3 py-2">
      <Link
        href={backHref}
        className="text-sm text-ink-400 transition-colors hover:text-gold-300"
      >
        {backLabel}
      </Link>

      <div className="flex items-center gap-2">
        {downloadHref && (
          <a
            href={downloadHref}
            className="rounded-lg border border-ink-700 px-3 py-1.5 text-sm text-ink-300 transition-colors hover:border-gold-500/60 hover:text-gold-300"
          >
            Descargar PDF
          </a>
        )}
        <button
          type="button"
          onClick={() => window.print()}
          className="rounded-lg border border-gold-500/50 bg-gold-500/10 px-3 py-1.5 text-sm text-gold-300 transition-colors hover:border-gold-500/80 hover:bg-gold-500/15"
        >
          Guardar como PDF
        </button>
      </div>
    </div>
  );
}
