import type { Metadata } from "next";
import { DiagramsScreen } from "@/components/docs/DiagramsScreen";

// Igual que el resto de la documentación: se lee en cada request para mostrar
// siempre los diagramas que están en los archivos.
export const dynamic = "force-dynamic";

export const metadata: Metadata = {
  title: "Diagramas de flujo — Documentación",
  description:
    "Todos los diagramas de la documentación del ajedrez, a ancho completo.",
};

export default function DiagramsPage() {
  return <DiagramsScreen />;
}
