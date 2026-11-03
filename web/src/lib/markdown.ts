export type Heading = {
  id: string;
  text: string;
  level: number;
};

export type Diagram = {
  id: string;
  chart: string;
  title: string;
  sectionId?: string;
};

// Mismo criterio que GitHub: sin acentos, todo en minúsculas y separado por
// guiones. Lo usan el índice y los títulos renderizados para coincidir.
export function slugify(value: string): string {
  return value
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "")
    .slice(0, 80);
}

// Saca el formato inline (`código`, **negrita**, [links]) para que el texto
// del índice sea el mismo que el del título ya renderizado.
function plainText(value: string): string {
  return value
    .replace(/!\[([^\]]*)\]\([^)]*\)/g, "$1")
    .replace(/\[([^\]]*)\]\([^)]*\)/g, "$1")
    .replace(/`([^`]*)`/g, "$1")
    .replace(/[*_~]/g, "")
    .replace(/<[^>]+>/g, "")
    .trim();
}

export function extractHeadings(markdown: string): Heading[] {
  const headings: Heading[] = [];

  for (const line of markdown.split(/\r?\n/)) {
    const match = /^(#{2,3})\s+(.+?)\s*#*\s*$/.exec(line);
    if (!match) {
      continue;
    }

    const text = plainText(match[2]);
    if (!text) {
      continue;
    }

    headings.push({
      id: slugify(text),
      text,
      level: match[1].length,
    });
  }

  return headings;
}

// El título grande lo pone la pantalla, así que el markdown arranca sin su H1.
export function stripTitle(markdown: string): string {
  return markdown.replace(/^#\s+.*(?:\r?\n)+/, "").trimStart();
}

// Separa los diagramas Mermaid del texto. En el artículo queda un marcador
// `[[diagrama-N]]` que DocMarkdown convierte en un enlace, y los diagramas se
// dibujan al final de la página con todo el ancho disponible. El título de cada
// diagrama es el último encabezado que aparece antes del bloque.
export function extractDiagrams(markdown: string): {
  body: string;
  diagrams: Diagram[];
} {
  const lines = markdown.split(/\r?\n/);
  const body: string[] = [];
  const diagrams: Diagram[] = [];

  let heading = "";
  let sectionId: string | undefined;
  let fence: string | null = null;
  let buffer: string[] = [];

  for (const line of lines) {
    const open = /^\s*```([\w-]*)\s*$/.exec(line);

    if (fence === null && open) {
      fence = open[1] || "";
      buffer = [];
      // Los bloques que no son Mermaid se dejan tal cual.
      if (fence !== "mermaid") {
        body.push(line);
      }
      continue;
    }

    if (fence !== null && /^\s*```\s*$/.test(line)) {
      if (fence === "mermaid") {
        const index = diagrams.length + 1;
        diagrams.push({
          id: `diagrama-${index}`,
          chart: buffer.join("\n").trim(),
          title: heading || `Diagrama ${index}`,
          sectionId,
        });
        body.push(`[[diagrama-${index}]]`, "");
      } else {
        body.push(line);
      }

      fence = null;
      buffer = [];
      continue;
    }

    if (fence !== null) {
      if (fence === "mermaid") {
        buffer.push(line);
      } else {
        body.push(line);
      }
      continue;
    }

    const title = /^(#{2,4})\s+(.+?)\s*#*\s*$/.exec(line);

    if (title) {
      heading = plainText(title[2]);
      sectionId = slugify(heading);
    }

    body.push(line);
  }

  return { body: body.join("\n"), diagrams };
}
