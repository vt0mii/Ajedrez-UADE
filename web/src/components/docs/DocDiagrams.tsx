import type { Diagram } from "@/lib/markdown";
import { MermaidDiagram } from "./MermaidDiagram";

// Sección de diagramas. Vive fuera de la grilla de lectura para que cada
// dibujo use todo el ancho de la página y no comparta espacio con el índice.
export function DocDiagrams({ diagrams }: { diagrams: Diagram[] }) {
  if (diagrams.length === 0) {
    return null;
  }

  return (
    <section id="diagramas" className="scroll-mt-24 border-t border-ink-800/70 pt-10">
      <header className="max-w-3xl">
        <p className="text-[10px] font-semibold tracking-[0.16em] text-gold-400 uppercase">
          Diagramas
        </p>
        <h2 className="mt-2 font-serif text-2xl tracking-tight text-ink-100 sm:text-3xl">
          Los diagramas de este documento
        </h2>
        <p className="mt-3 text-sm leading-relaxed text-ink-300">
          Están reunidos acá para que ocupen el ancho completo. En el texto
          dejamos un enlace a cada uno y, desde el título, podés volver a la
          sección original.
        </p>
      </header>

      <div className="mt-8 space-y-10">
        {diagrams.map((diagram, index) => (
          <figure key={diagram.id} id={diagram.id} className="scroll-mt-24">
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
                <a
                  href={`#${diagram.sectionId}`}
                  className="text-xs text-ink-400 transition-colors hover:text-gold-300"
                >
                  Volver al texto ↑
                </a>
              )}
            </figcaption>

            <MermaidDiagram chart={diagram.chart} variant="gallery" />
          </figure>
        ))}
      </div>
    </section>
  );
}
