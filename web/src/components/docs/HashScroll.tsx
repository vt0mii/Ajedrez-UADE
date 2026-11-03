"use client";

import { useEffect } from "react";

// Los enlaces del tipo `/docs/diagramas#justificacion-diagrama-3` se resuelven
// antes de que Mermaid dibuje: en ese momento la página todavía es más corta y
// el ancla queda corrida. Cada diagrama avisa cuando terminó de renderizar y
// acá volvemos a alinear la posición hasta que el último termina.
export function HashScroll() {
  useEffect(() => {
    function align() {
      const hash = decodeURIComponent(window.location.hash.slice(1));

      if (!hash) {
        return;
      }

      document.getElementById(hash)?.scrollIntoView({ block: "start" });
    }

    align();
    window.addEventListener("mermaid:rendered", align);

    return () => window.removeEventListener("mermaid:rendered", align);
  }, []);

  return null;
}
