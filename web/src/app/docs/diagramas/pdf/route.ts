import { DIAGRAMS_PDF, readExport } from "@/lib/docs";

// Sirve el PDF ya exportado de la pantalla de diagramas. Se lee en cada
// request así, si se regenera el archivo, la descarga queda al día sin rebuild.
export const dynamic = "force-dynamic";

export function GET() {
  const pdf = readExport(DIAGRAMS_PDF);

  if (!pdf) {
    return new Response(
      "El PDF todavía no está generado. Corré `npm run export:pdf` en web/.",
      { status: 404, headers: { "Content-Type": "text/plain; charset=utf-8" } },
    );
  }

  return new Response(new Uint8Array(pdf), {
    headers: {
      "Content-Type": "application/pdf",
      "Content-Disposition": `attachment; filename="${DIAGRAMS_PDF}"`,
      "Content-Length": String(pdf.byteLength),
      "Cache-Control": "no-store",
    },
  });
}
