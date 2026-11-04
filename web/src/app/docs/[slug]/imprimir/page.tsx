import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { DocMarkdown } from "@/components/docs/DocMarkdown";
import { PrintToolbar } from "@/components/docs/PrintToolbar";
import { findDoc, readDoc } from "@/lib/docs";
import { stripTitle } from "@/lib/markdown";

// Versión imprimible de un documento. Muestra los diagramas dentro del texto
// (no las tarjetas) y esconde el menú, el índice y el pie del sitio al imprimir.
export const dynamic = "force-dynamic";

export async function generateMetadata(
  props: PageProps<"/docs/[slug]/imprimir">,
): Promise<Metadata> {
  const { slug } = await props.params;
  const doc = findDoc(slug);

  if (!doc) {
    return { title: "Documento no encontrado — Ajedrez" };
  }

  return {
    title: `${doc.title} — Versión imprimible`,
  };
}

export default async function PrintDocPage(
  props: PageProps<"/docs/[slug]/imprimir">,
) {
  const { slug } = await props.params;
  const doc = findDoc(slug);

  if (!doc) {
    notFound();
  }

  const raw = readDoc(doc);
  const body = raw ? stripTitle(raw) : "";

  return (
    <div className="print-page mx-auto w-full max-w-[860px] px-4 py-8 sm:px-6 lg:py-12">
      <PrintToolbar backHref={`/docs/${doc.slug}`} />

      <header className="mt-8 border-b border-ink-800 pb-6">
        <p className="text-[10px] font-semibold tracking-[0.16em] text-gold-400 uppercase">
          Ingeniería de Software · UADE
        </p>
        <h1 className="mt-2 font-serif text-4xl tracking-tight text-ink-100">
          {doc.title}
        </h1>
        <p className="mt-3 text-sm leading-relaxed text-ink-300">
          {doc.description}
        </p>
        <p className="mt-4 text-xs text-ink-400">
          Medina Cotos · Tortora · Umerez · Quintana
        </p>
      </header>

      {raw ? (
        <article className="doc-content mt-8">
          <DocMarkdown content={body} diagramVariant="print" />
        </article>
      ) : (
        <p className="mt-8 rounded-lg border border-danger/40 bg-danger/5 px-4 py-3 text-sm text-danger">
          No se pudo leer <code>{doc.path}</code>.
        </p>
      )}

      <footer className="mt-12 border-t border-ink-800 pt-4 text-xs text-ink-400">
        {doc.path} · Ajedrez · Medina Cotos, Tortora, Umerez y Quintana
      </footer>
    </div>
  );
}
