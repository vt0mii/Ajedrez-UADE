import Link from "next/link";
import { DOCS } from "@/lib/docs";

// Menú de documentos. En pantallas chicas se vuelve una fila con scroll;
// desde `lg` pasa a ser una columna fija al costado del contenido.
export function DocsNav({ activeSlug }: { activeSlug: string }) {
  return (
    <nav aria-label="Documentos">
      <p className="mb-3 text-[10px] font-semibold tracking-[0.16em] text-ink-400 uppercase">
        Documentos
      </p>
      <ul className="scroll-thin flex gap-2 overflow-x-auto pb-2 lg:flex-col lg:gap-1.5 lg:overflow-visible lg:pb-0">
        {DOCS.map((doc) => {
          const active = doc.slug === activeSlug;

          return (
            <li key={doc.slug} className="shrink-0 lg:shrink">
              <Link
                href={`/docs/${doc.slug}`}
                aria-current={active ? "page" : undefined}
                className={`block rounded-lg border px-3 py-2 transition-colors ${
                  active
                    ? "border-gold-500/60 bg-gold-500/10 text-gold-300"
                    : "border-transparent text-ink-300 hover:border-ink-700 hover:bg-ink-900/60 hover:text-ink-100"
                }`}
              >
                <span className="text-sm font-medium whitespace-nowrap lg:whitespace-normal">
                  {doc.title}
                </span>
                <span className="mt-0.5 hidden font-mono text-[10px] text-ink-400 lg:block">
                  {doc.path}
                </span>
              </Link>
            </li>
          );
        })}

        {/* Los diagramas también tienen su propia pantalla, fuera de la
            columna de lectura. */}
        <li className="shrink-0 border-ink-800/70 lg:mt-2 lg:shrink lg:border-t lg:pt-3">
          <Link
            href="/docs/diagramas"
            aria-current={activeSlug === "diagramas" ? "page" : undefined}
            className="block rounded-lg border border-transparent px-3 py-2 text-ink-300 transition-colors hover:border-ink-700 hover:bg-ink-900/60 hover:text-ink-100"
          >
            <span className="text-sm font-medium whitespace-nowrap lg:whitespace-normal">
              Diagramas de flujo
            </span>
            <span className="mt-0.5 hidden font-mono text-[10px] text-ink-400 lg:block">
              Todos los diagramas
            </span>
          </Link>
        </li>
      </ul>
    </nav>
  );
}
