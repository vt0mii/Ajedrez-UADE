import Link from "next/link";
import type { ReactNode } from "react";
import { isValidElement } from "react";
import Markdown from "react-markdown";
import type { Components } from "react-markdown";
import remarkGfm from "remark-gfm";
import { docHref } from "@/lib/docs";
import { slugify } from "@/lib/markdown";
import type { Diagram } from "@/lib/markdown";
import { MermaidDiagram } from "./MermaidDiagram";

// Saca el texto plano de los hijos de un título, incluso si adentro hay
// código, negrita o links. Así el id coincide con el del índice.
function reactText(node: ReactNode): string {
  if (node === null || node === undefined || typeof node === "boolean") {
    return "";
  }
  if (typeof node === "string" || typeof node === "number") {
    return String(node);
  }
  if (Array.isArray(node)) {
    return node.map(reactText).join("");
  }
  if (isValidElement<{ children?: ReactNode }>(node)) {
    return reactText(node.props.children);
  }
  return "";
}

// Saca el lenguaje del bloque (`powershell`, `java`, `mermaid`, etc.) para
// mostrarlo en la barra superior del bloque de código.
function codeLanguage(children: ReactNode): string | undefined {
  const nodes = Array.isArray(children) ? children : [children];

  for (const child of nodes) {
    if (isValidElement<{ className?: string }>(child)) {
      const match = child.props.className?.match(/language-([\w-]+)/);
      if (match) {
        return match[1];
      }
    }
  }

  return undefined;
}

// En el artículo, cada diagrama Mermaid deja un marcador `[[diagrama-N]]`.
// Acá se convierte en una tarjeta que lleva a la pantalla de diagramas.
function diagramLink(
  diagrams: Diagram[],
  marker: string,
  hrefFor: (diagram: Diagram) => string,
) {
  const index = diagrams.findIndex((diagram) => diagram.id === marker);

  if (index < 0) {
    return null;
  }

  const diagram = diagrams[index];

  return (
    <Link
      href={hrefFor(diagram)}
      className="doc-diagram-link group"
    >
      <span className="doc-diagram-link-label">Diagrama {index + 1}</span>
      <span className="doc-diagram-link-title">{diagram.title}</span>
      <span className="doc-diagram-link-cta">Ver el diagrama →</span>
    </Link>
  );
}

function createComponents(
  diagrams: Diagram[],
  hrefFor: (diagram: Diagram) => string,
): Components {
  return {
    h2: ({ children }) => <h2 id={slugify(reactText(children))}>{children}</h2>,
    h3: ({ children }) => <h3 id={slugify(reactText(children))}>{children}</h3>,
    h4: ({ children }) => <h4 id={slugify(reactText(children))}>{children}</h4>,

    // El marcador del diagrama viene solo en un párrafo, así que se reemplaza
    // por la tarjeta sin tocar la estructura del resto del texto.
    p: ({ children }) => {
      const marker = /^\[\[(diagrama-\d+)\]\]$/.exec(reactText(children).trim());
      const card = marker ? diagramLink(diagrams, marker[1], hrefFor) : null;

      return card ?? <p>{children}</p>;
    },

    // Los bloques de código van dentro de una tarjeta con el lenguaje arriba.
    // Los de Mermaid se dibujan como diagramas y el resto queda como código.
    pre: ({ children }) => {
      const language = codeLanguage(children);

      if (language === "mermaid") {
        return <MermaidDiagram chart={reactText(children).trim()} />;
      }

      return (
        <div className="doc-code">
          {language && <span className="doc-code-lang">{language}</span>}
          <pre>{children}</pre>
        </div>
      );
    },

    // Los links entre documentos (`docs/...`, `web/README.md`) se convierten en
    // rutas de la web. Lo externo se abre aparte; el resto queda igual.
    a: ({ href, title, children }) => {
      const internal = href ? docHref(href) : undefined;

      if (internal) {
        return (
          <Link href={internal} title={title}>
            {children}
          </Link>
        );
      }

      const external = Boolean(href && /^https?:\/\//i.test(href));

      return (
        <a
          href={href}
          title={title}
          target={external ? "_blank" : undefined}
          rel={external ? "noreferrer" : undefined}
        >
          {children}
        </a>
      );
    },

    // Las tablas del markdown pueden ser más anchas que la columna; el wrapper
    // les da scroll horizontal sin romper el layout.
    table: ({ children }) => (
      <div className="doc-table">
        <table>{children}</table>
      </div>
    ),
  };
}

export function DocMarkdown({
  content,
  diagrams = [],
  diagramHref,
}: {
  content: string;
  diagrams?: Diagram[];
  // Destino de la tarjeta que reemplaza a cada diagrama en el texto. Por
  // defecto baja al ancla local; la pantalla de docs lo apunta a /docs/diagramas.
  diagramHref?: (diagram: Diagram) => string;
}) {
  const hrefFor = diagramHref ?? ((diagram: Diagram) => `#${diagram.id}`);

  return (
    <Markdown remarkPlugins={[remarkGfm]} components={createComponents(diagrams, hrefFor)}>
      {content}
    </Markdown>
  );
}
