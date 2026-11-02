import type { Metadata } from "next";
import { DocsScreen } from "@/components/docs/DocsScreen";

// La documentación se lee en cada request para que refleje los archivos
// actuales, incluso después de un build.
export const dynamic = "force-dynamic";

export const metadata: Metadata = {
  title: "Documentación — Ajedrez",
  description:
    "Arquitectura, decisiones de diseño y guías del trabajo práctico de Ingeniería de Software.",
};

export default function DocsPage() {
  return <DocsScreen />;
}
