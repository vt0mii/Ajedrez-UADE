import type { Metadata } from "next";
import { Geist, Geist_Mono, Instrument_Serif } from "next/font/google";
import { cookies } from "next/headers";
import Link from "next/link";
import { ThemeToggle } from "@/components/ui/ThemeToggle";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

const instrumentSerif = Instrument_Serif({
  variable: "--font-instrument-serif",
  subsets: ["latin"],
  weight: "400",
});

export const metadata: Metadata = {
  title: "Ajedrez UADE",
  description: "Ajedrez con arquitectura hexagonal: dominio Java y frontend Next.js.",
};

export default async function RootLayout({ children }: LayoutProps<"/">) {
  // Guardamos el tema en una cookie así el HTML ya sale bien
  // y no hay flash al cargar. Sin cookie, arranca en oscuro.
  const storedTheme = (await cookies()).get("chess-theme")?.value;
  const theme = storedTheme === "light" ? "light" : "dark";

  return (
    <html
      lang="es"
      data-theme={theme}
      suppressHydrationWarning
      className={`${geistSans.variable} ${geistMono.variable} ${instrumentSerif.variable} h-full antialiased`}
    >
      <body className="flex min-h-full flex-col font-sans transition-colors duration-200">
        <header className="border-b border-ink-800/80">
          <div className="mx-auto flex w-full max-w-[1400px] items-center justify-between gap-3 px-4 py-3 sm:px-6">
            <Link href="/" className="group flex items-center gap-2.5">
              <span className="piece text-xl leading-none text-gold-400 transition-transform group-hover:-rotate-6">
                ♞
              </span>
              <span className="text-sm font-semibold tracking-tight text-ink-100">
                Ajedrez UADE
              </span>
            </Link>
            <div className="flex items-center gap-2">
              <span className="hidden rounded-full border border-ink-800 px-2.5 py-1 text-[10px] font-medium tracking-[0.14em] text-ink-400 uppercase sm:inline">
                Puertos y adaptadores
              </span>
              <ThemeToggle />
            </div>
          </div>
        </header>

        <div className="flex flex-1 flex-col">{children}</div>

        <footer className="border-t border-ink-800/60">
          <div className="mx-auto flex w-full max-w-[1400px] flex-col gap-1 px-4 py-4 text-xs text-ink-400 sm:flex-row sm:items-center sm:justify-between sm:px-6">
            <span>Trabajo práctico de Ingeniería de Software · UADE</span>
            <span>Java 17 · Next.js · Tailwind CSS</span>
          </div>
        </footer>
      </body>
    </html>
  );
}
