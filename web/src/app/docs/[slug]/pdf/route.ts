import { findDoc, readExport } from "@/lib/docs";

// Descarga del PDF de un documento. El nombre del archivo sale de la lista
// fija de documentos, nunca de la URL, así que no se puede pedir otro archivo.
export const dynamic = "force-dynamic";

export async function GET(
  _request: Request,
  ctx: RouteContext<"/docs/[slug]/pdf">,
) {
  const { slug } = await ctx.params;
  const doc = findDoc(slug);
  const pdf = doc?.pdf ? readExport(doc.pdf) : null;

  if (!doc?.pdf || !pdf) {
    return new Response(
      "El PDF todavía no está generado. Corré `npm run export:pdf` en web/.",
      {
        status: 404,
        headers: { "Content-Type": "text/plain; charset=utf-8" },
      },
    );
  }

  return new Response(new Uint8Array(pdf), {
    headers: {
      "Content-Type": "application/pdf",
      "Content-Disposition": `attachment; filename="${doc.pdf}"`,
      "Content-Length": String(pdf.byteLength),
      "Cache-Control": "no-store",
    },
  });
}
