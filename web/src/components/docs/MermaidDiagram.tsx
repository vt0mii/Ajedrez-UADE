"use client";

import { useEffect, useId, useRef, useState } from "react";

type DiagramTheme = "dark" | "default";

type DiagramSize = { width: number; height: number };

// Lee el tamaño real del SVG para decidir si el diagrama entra en la columna o
// si conviene dejarlo a tamaño natural y permitir scroll dentro de la tarjeta.
function readDiagramSize(svg: string): DiagramSize | null {
  const match = /viewBox="([-\d.]+) ([-\d.]+) ([\d.]+) ([\d.]+)"/.exec(svg);

  if (!match) {
    return null;
  }

  return { width: Number(match[3]), height: Number(match[4]) };
}

// Renderiza un bloque ```mermaid``` del markdown. Mermaid se carga solo en el
// cliente y solo cuando aparece un diagrama, así no pesa en el resto del sitio.
export function MermaidDiagram({ chart }: { chart: string }) {
  const reactId = useId().replace(/[^a-zA-Z0-9_-]/g, "");
  const renderCount = useRef(0);

  const [theme, setTheme] = useState<DiagramTheme>("dark");
  const [svg, setSvg] = useState<string | null>(null);
  const [size, setSize] = useState<DiagramSize | null>(null);
  const [failed, setFailed] = useState(false);

  // El botón de tema cambia `data-theme` en <html>. Escuchamos ese cambio para
  // volver a dibujar el diagrama con los colores correctos.
  useEffect(() => {
    const root = document.documentElement;

    function syncTheme() {
      setTheme(root.dataset.theme === "light" ? "default" : "dark");
    }

    syncTheme();
    const observer = new MutationObserver(syncTheme);
    observer.observe(root, { attributes: true, attributeFilter: ["data-theme"] });

    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    let active = true;

    async function render() {
      try {
        const mermaid = (await import("mermaid")).default;
        const id = `mermaid-${reactId}-${renderCount.current++}`;

        mermaid.initialize({
          startOnLoad: false,
          securityLevel: "strict",
          theme,
          fontFamily: "var(--font-geist-sans), ui-sans-serif, sans-serif",
          themeVariables: {
            background: "transparent",
            fontSize: "14px",
          },
        });

        const result = await mermaid.render(id, chart);
        if (active) {
          setSvg(result.svg);
          setSize(readDiagramSize(result.svg));
          setFailed(false);
        }
      } catch {
        if (active) {
          setFailed(true);
        }
      }
    }

    void render();

    return () => {
      active = false;
    };
  }, [chart, theme, reactId]);

  if (failed) {
    return (
      <div className="doc-code">
        <span className="doc-code-lang">mermaid (no se pudo dibujar)</span>
        <pre>{chart}</pre>
      </div>
    );
  }

  // Un diagrama más ancho que la columna se muestra a tamaño natural y con
  // scroll propio; si entra, se ajusta al ancho como una imagen.
  const wide = Boolean(size && size.width > 1100);

  return (
    <div className="doc-mermaid">
      <span className="doc-code-lang">
        mermaid{wide ? " · desplazá para verlo completo" : ""}
      </span>
      <div
        className="doc-mermaid-body"
        data-wide={wide ? "true" : undefined}
        style={
          wide
            ? {
                maxHeight: "76vh",
                overflow: "auto",
                overscrollBehavior: "contain",
              }
            : undefined
        }
      >
        {svg ? (
          wide && size ? (
            <div
              className="doc-mermaid-canvas"
              style={{ width: size.width }}
              dangerouslySetInnerHTML={{ __html: svg }}
            />
          ) : (
            <div dangerouslySetInnerHTML={{ __html: svg }} />
          )
        ) : (
          <span className="text-sm text-ink-400">Cargando diagrama…</span>
        )}
      </div>
    </div>
  );
}
