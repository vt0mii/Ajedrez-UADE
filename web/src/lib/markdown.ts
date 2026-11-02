export type Heading = {
  id: string;
  text: string;
  level: number;
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
