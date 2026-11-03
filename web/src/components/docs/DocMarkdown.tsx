import Link from "next/link";
import type { ReactNode } from "react";
import { isValidElement } from "react";
import Markdown from "react-markdown";
import type { Components } from "react-markdown";
import remarkGfm from "remark-gfm";
import { docHref } from "@/lib/docs";
import { slugify } from "@/lib/markdown";
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

const COMPONENTS: Components = {
  h2: ({ children }) => <h2 id={slugify(reactText(children))}>{children}</h2>,
  h3: ({ children }) => <h3 id={slugify(reactText(children))}>{children}</h3>,
  h4: ({ children }) => <h4 id={slugify(reactText(children))}>{children}</h4>,

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

export function DocMarkdown({ content }: { content: string }) {
  return (
    <Markdown remarkPlugins={[remarkGfm]} components={COMPONENTS}>
      {content}
    </Markdown>
  );
}
