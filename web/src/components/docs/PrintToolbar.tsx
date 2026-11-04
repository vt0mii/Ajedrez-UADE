import Link from "next/link";

// Barra de la versión imprimible. No sale en el PDF: solo sirve en pantalla
// para volver al documento.
export function PrintToolbar({
  backHref,
  backLabel = "← Volver al documento",
}: {
  backHref: string;
  backLabel?: string;
}) {
  return (
    <div className="no-print flex flex-wrap items-center justify-between gap-2 rounded-lg border border-ink-800 bg-ink-900/60 px-3 py-2">
      <Link
        href={backHref}
        className="text-sm text-ink-400 transition-colors hover:text-gold-300"
      >
        {backLabel}
      </Link>
    </div>
  );
}
