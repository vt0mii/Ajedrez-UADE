import type { Heading } from "@/lib/markdown";

// Índice lateral del documento. Los ids salen del mismo slug que usan los
// títulos renderizados, así los enlaces internos funcionan sin JavaScript.
export function DocsToc({ headings }: { headings: Heading[] }) {
  if (headings.length === 0) {
    return null;
  }

  return (
    <nav aria-label="Contenido del documento">
      <p className="mb-3 text-[10px] font-semibold tracking-[0.16em] text-ink-400 uppercase">
        En esta página
      </p>
      <ul className="scroll-thin max-h-[calc(100vh-8rem)] space-y-1 overflow-y-auto border-l border-ink-800 pl-3">
        {headings.map((heading) => (
          <li
            key={heading.id}
            className={heading.level === 3 ? "pl-3" : undefined}
          >
            <a
              href={`#${heading.id}`}
              className="block text-sm leading-snug text-ink-400 transition-colors hover:text-gold-300"
            >
              {heading.text}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
}
