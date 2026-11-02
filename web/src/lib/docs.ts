import { existsSync, readFileSync } from "node:fs";
import path from "node:path";

export type DocMeta = {
  slug: string;
  title: string;
  description: string;
  // Ruta relativa a la raíz del repo. Se usa para leer el archivo y también
  // para mostrarla en la pantalla, así se sabe de dónde sale cada documento.
  path: string;
};

// La lista es fija a propósito: ordena el menú y evita que un slug inventado
// termine leyendo cualquier archivo del disco.
export const DOCS: readonly DocMeta[] = [
  {
    slug: "arquitectura",
    title: "Arquitectura",
    description:
      "Cómo se conectan el dominio, la aplicación, la infraestructura y la web.",
    path: "docs/ARQUITECTURA.md",
  },
  {
    slug: "justificacion",
    title: "Justificación de diseño",
    description:
      "Las decisiones del trabajo práctico y por qué las tomamos así.",
    path: "docs/JUSTIFICACION.md",
  },
  {
    slug: "diseno-front",
    title: "Diseño del frontend",
    description:
      "Contrato de la API, pantallas, temas y criterios visuales de la web.",
    path: "docs/DISENO-FRONT.md",
  },
  {
    slug: "readme",
    title: "README",
    description:
      "Cómo compilar, correr los tests y jugar desde la consola o la web.",
    path: "README.md",
  },
  {
    slug: "web",
    title: "README del frontend",
    description:
      "Requisitos, scripts, estructura y atajos de la aplicación Next.js.",
    path: "web/README.md",
  },
];

export function findDoc(slug: string): DocMeta | undefined {
  return DOCS.find((doc) => doc.slug === slug);
}

// Durante `next dev` y `next build` el proceso corre desde `web/`, pero los
// documentos viven en la raíz del repo. Probamos los dos lugares para que
// funcione igual si alguien levanta Next desde otro lado.
function repoRoot(): string {
  const candidates = [process.cwd(), path.resolve(process.cwd(), "..")];
  const root = candidates.find((dir) =>
    existsSync(path.join(dir, "docs", "ARQUITECTURA.md")),
  );
  return root ?? process.cwd();
}

export function readDoc(doc: DocMeta): string | null {
  try {
    // El comentario le dice a Turbopack que no intente rastrear todo el repo
    // por una ruta armada en runtime.
    return readFileSync(
      /* turbopackIgnore: true */
      path.join(/* turbopackIgnore: true */ repoRoot(), doc.path),
      "utf8",
    );
  } catch {
    return null;
  }
}

// Los documentos se enlazan entre ellos con rutas del repo (`docs/...`,
// `web/README.md`). En la web esos links tienen que apuntar a la pantalla de
// documentación, no al archivo crudo.
export function docHref(href: string): string | undefined {
  if (!href || href.startsWith("#") || /^[a-z][a-z0-9+.-]*:/i.test(href)) {
    return undefined;
  }

  const [rawPath, hash] = href.split("#");
  const clean = rawPath.replace(/^\.\//, "").replace(/^\/+/, "");
  if (!clean.toLowerCase().endsWith(".md")) {
    return undefined;
  }

  const target = DOCS.find((doc) => {
    const fileName = doc.path.split("/").pop();
    return clean === doc.path || clean === fileName;
  });

  if (!target) {
    return undefined;
  }

  return `/docs/${target.slug}${hash ? `#${hash}` : ""}`;
}
