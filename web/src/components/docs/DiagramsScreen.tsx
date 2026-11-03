import Link from "next/link";
import { DOCS, readDoc } from "@/lib/docs";
import type { DocMeta } from "@/lib/docs";
import { extractDiagrams, stripTitle } from "@/lib/markdown";
import type { Diagram } from "@/lib/markdown";
import { HashScroll } from "./HashScroll";
import { MermaidDiagram } from "./MermaidDiagram";

type DiagramGroup = {
  doc: DocMeta;
  diagrams: Diagram[];
};

// Junta los diagramas de todos los documentos. Cada uno se dibuja acá, fuera
// de la columna de lectura, para que pueda ocupar todo el ancho de la página.
function collectDiagrams(): DiagramGroup[] {
  return DOCS.map((doc) => {
    const raw = readDoc(doc);
    const diagrams = raw
      ? extractDiagrams(stripTitle(raw)).diagrams
      : [];

    return { doc, diagrams };
  }).filter((group) => group.diagrams.length > 0);
}

export function DiagramsScreen() {
  const groups = collectDiagrams();
  const total = groups.reduce((sum, group) => sum + group.diagrams.length, 0);

  return (
    <div className="mx-auto w-full max-w-[1600px] px-4 py-8 sm:px-6 lg:py-12">
      <HashScroll />

      <header>
        <Link
          href="/docs"
          className="inline-flex items-center gap-2 text-sm text-ink-400 transition-colors hover:text-gold-300"
        >
          <span aria-hidden>←</span> Documentación
        </Link>

        <p className="mt-6 text-[10px] font-semibold tracking-[0.16em] text-gold-400 uppercase">
          Documentación
        </p>
        <h1 className="mt-2 font-serif text-3xl tracking-tight text-ink-100 sm:text-4xl">
          Diagramas de flujo
        </h1>
        <p className="mt-3 max-w-3xl text-sm leading-relaxed text-ink-300">
          Los {total} diagramas de los documentos del proyecto, reunidos en una
          sola pantalla. Cada uno tiene el ancho completo disponible y un enlace
          para volver a la sección del texto donde se explica.
        </p>

        {groups.length > 0 && (
          <nav
            aria-label="Documentos con diagramas"
            className="mt-6 flex flex-wrap gap-2"
          >
            {groups.map((group) => (
              <a
                key={group.doc.slug}
                href={`#doc-${group.doc.slug}`}
                className="inline-flex items-center gap-2 rounded-lg border border-ink-800 px-3 py-1.5 text-sm text-ink-300 transition-colors hover:border-gold-500/60 hover:bg-gold-500/5 hover:text-gold-300"
              >
                {group.doc.title}
                <span className="font-mono text-[10px] text-ink-400">
                  {group.diagrams.length}
                </span>
              </a>
            ))}
          </nav>
        )}
      </header>

      {groups.map((group) => (
        <section
          key={group.doc.slug}
          id={`doc-${group.doc.slug}`}
          className="mt-16 scroll-mt-8 border-t border-ink-800/70 pt-8"
        >
          <header className="flex flex-wrap items-end justify-between gap-x-6 gap-y-3">
            <div>
              <p className="font-mono text-[10px] tracking-[0.12em] text-ink-400 uppercase">
                {group.doc.path}
              </p>
              <h2 className="mt-1 font-serif text-2xl tracking-tight text-ink-100 sm:text-3xl">
                {group.doc.title}
              </h2>
            </div>

            <Link
              href={`/docs/${group.doc.slug}`}
              className="text-sm text-ink-400 transition-colors hover:text-gold-300"
            >
              Leer el documento →
            </Link>
          </header>

          <div className="mt-8 space-y-12">
            {group.diagrams.map((diagram, index) => (
              <figure
                key={diagram.id}
                id={`${group.doc.slug}-${diagram.id}`}
                className="scroll-mt-8"
              >
                <figcaption className="mb-3 flex flex-wrap items-end justify-between gap-x-6 gap-y-2 border-b border-ink-800/70 pb-2">
                  <div>
                    <p className="font-mono text-[10px] font-semibold tracking-[0.16em] text-gold-400 uppercase">
                      Diagrama {index + 1}
                    </p>
                    <h3 className="mt-1 text-sm font-semibold text-ink-100">
                      {diagram.title}
                    </h3>
                  </div>

                  {diagram.sectionId && (
                    <Link
                      href={`/docs/${group.doc.slug}#${diagram.sectionId}`}
                      className="text-xs text-ink-400 transition-colors hover:text-gold-300"
                    >
                      Ver en el texto ↑
                    </Link>
                  )}
                </figcaption>

                <MermaidDiagram chart={diagram.chart} variant="gallery" />
              </figure>
            ))}
          </div>
        </section>
      ))}
    </div>
  );
}
