import Link from "next/link";
import { notFound } from "next/navigation";
import { DOCS, findDoc, readDoc } from "@/lib/docs";
import { extractDiagrams, extractHeadings, stripTitle } from "@/lib/markdown";
import { DocMarkdown } from "./DocMarkdown";
import { DocsNav } from "./DocsNav";
import { DocsToc } from "./DocsToc";

// Pantalla de documentación. Lee el markdown en el servidor y lo muestra con
// el mismo formato que el resto del sitio. No hay copia en la base ni estado
// de cliente: si cambia el archivo, cambia la página.
export function DocsScreen({ slug }: { slug?: string }) {
  const activeSlug = slug ?? DOCS[0].slug;
  const doc = findDoc(activeSlug);

  if (!doc) {
    notFound();
  }

  const raw = readDoc(doc);
  const { body, diagrams } = raw
    ? extractDiagrams(stripTitle(raw))
    : { body: "", diagrams: [] };
  const headings = extractHeadings(body);

  const index = DOCS.findIndex((item) => item.slug === doc.slug);
  const previous = index > 0 ? DOCS[index - 1] : undefined;
  const next = index >= 0 && index < DOCS.length - 1 ? DOCS[index + 1] : undefined;

  return (
    <div className="mx-auto w-full max-w-[1400px] px-4 py-8 sm:px-6 lg:py-12">
      <div className="grid gap-8 lg:grid-cols-[240px_minmax(0,1fr)] xl:grid-cols-[240px_minmax(0,1fr)_220px]">
        <aside className="lg:sticky lg:top-6 lg:self-start">
          <DocsNav activeSlug={doc.slug} />
        </aside>

        <main className="min-w-0">
          <header className="border-b border-ink-800/70 pb-6">
            <p className="text-[10px] font-semibold tracking-[0.16em] text-gold-400 uppercase">
              Documentación
            </p>
            <h1 className="mt-2 font-serif text-3xl tracking-tight text-ink-100 sm:text-4xl">
              {doc.title}
            </h1>
            <p className="mt-3 max-w-2xl text-sm leading-relaxed text-ink-300">
              {doc.description}
            </p>
            <p className="mt-3 font-mono text-[11px] text-ink-400">
              {doc.path}
            </p>

            {doc.pdf && (
              <div className="mt-4 flex flex-wrap items-center gap-2">
                <a
                  href={`/docs/${doc.slug}/pdf`}
                  className="inline-flex items-center gap-2 rounded-lg border border-gold-500/40 bg-gold-500/5 px-3 py-2 text-sm text-gold-300 transition-colors hover:border-gold-500/70 hover:bg-gold-500/10"
                >
                  Descargar PDF
                </a>
                <Link
                  href={`/docs/${doc.slug}/imprimir`}
                  className="inline-flex items-center gap-2 rounded-lg border border-ink-700 px-3 py-2 text-sm text-ink-300 transition-colors hover:border-ink-600 hover:text-ink-100"
                >
                  Versión imprimible
                </Link>
              </div>
            )}

            {diagrams.length > 0 && (
              <Link
                href={`/docs/diagramas#doc-${doc.slug}`}
                className="mt-3 inline-flex items-center gap-2 rounded-lg border border-gold-500/40 bg-gold-500/5 px-3 py-2 text-sm text-gold-300 transition-colors hover:border-gold-500/70 hover:bg-gold-500/10"
              >
                Ver los {diagrams.length} diagramas de este documento
                <span aria-hidden>→</span>
              </Link>
            )}
          </header>

          {raw ? (
            <article className="doc-content mt-8">
              <DocMarkdown
                content={body}
                diagrams={diagrams}
                diagramHref={(diagram) =>
                  `/docs/diagramas#${doc.slug}-${diagram.id}`
                }
              />
            </article>
          ) : (
            <p className="mt-8 rounded-lg border border-danger/40 bg-danger/5 px-4 py-3 text-sm text-danger">
              No se pudo leer <code>{doc.path}</code>. Verificá que el archivo
              siga en el repositorio.
            </p>
          )}

          {(previous || next) && (
            <nav
              aria-label="Documentos anterior y siguiente"
              className="mt-12 flex flex-col justify-between gap-3 border-t border-ink-800/70 pt-6 sm:flex-row"
            >
              {previous ? (
                <Link
                  href={`/docs/${previous.slug}`}
                  className="group flex flex-col rounded-lg border border-ink-800 px-4 py-3 transition-colors hover:border-ink-700 hover:bg-ink-900/60"
                >
                  <span className="text-[10px] font-semibold tracking-[0.14em] text-ink-400 uppercase">
                    Anterior
                  </span>
                  <span className="mt-1 text-sm text-ink-300 transition-colors group-hover:text-ink-100">
                    {previous.title}
                  </span>
                </Link>
              ) : (
                <span />
              )}

              {next && (
                <Link
                  href={`/docs/${next.slug}`}
                  className="group flex flex-col rounded-lg border border-ink-800 px-4 py-3 text-right transition-colors hover:border-ink-700 hover:bg-ink-900/60 sm:ml-auto"
                >
                  <span className="text-[10px] font-semibold tracking-[0.14em] text-ink-400 uppercase">
                    Siguiente
                  </span>
                  <span className="mt-1 text-sm text-ink-300 transition-colors group-hover:text-ink-100">
                    {next.title}
                  </span>
                </Link>
              )}
            </nav>
          )}
        </main>

        <aside className="hidden xl:sticky xl:top-6 xl:block xl:self-start">
          <DocsToc headings={headings} />
        </aside>
      </div>
    </div>
  );
}
