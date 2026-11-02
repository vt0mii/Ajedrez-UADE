import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { DocsScreen } from "@/components/docs/DocsScreen";
import { findDoc } from "@/lib/docs";

export const dynamic = "force-dynamic";

export async function generateMetadata(
  props: PageProps<"/docs/[slug]">,
): Promise<Metadata> {
  const { slug } = await props.params;
  const doc = findDoc(slug);

  if (!doc) {
    return { title: "Documento no encontrado — Ajedrez" };
  }

  return {
    title: `${doc.title} — Documentación`,
    description: doc.description,
  };
}

export default async function DocPage(props: PageProps<"/docs/[slug]">) {
  const { slug } = await props.params;

  if (!findDoc(slug)) {
    notFound();
  }

  return <DocsScreen slug={slug} />;
}
