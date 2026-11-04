import { existsSync, mkdirSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
import puppeteer from "puppeteer-core";

// Exporta la documentación a PDF usando el mismo HTML de la web. Se corre
// desde `web/` con `npm run export:pdf` y necesita el dev server levantado
// (o un `npm start`) porque navega las páginas como lo haría una persona.

const here = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.resolve(here, "..", "..");
const outputDir = path.join(repoRoot, "docs", "export");

const baseUrl = (
  process.env.DOCS_BASE_URL ?? "http://localhost:3000"
).replace(/\/$/, "");

const targets = [
  {
    url: "/docs/arquitectura/imprimir",
    file: "ARQUITECTURA.pdf",
    ready: ".doc-content",
  },
  {
    url: "/docs/justificacion/imprimir",
    file: "JUSTIFICACION.pdf",
    ready: ".doc-content",
  },
  {
    url: "/docs/diseno-front/imprimir",
    file: "DISENO-FRONT.pdf",
    ready: ".doc-content",
  },
  {
    // La pantalla de diagramas va apaisada para aprovechar el ancho.
    url: "/docs/diagramas",
    file: "DIAGRAMAS-DE-FLUJO.pdf",
    ready: "figure",
    landscape: true,
  },
];

// Edge es el navegador que está instalado en la máquina. puppeteer-core no
// baja uno propio, así que probamos las rutas habituales y dejamos que se
// pise con la variable de entorno si hace falta.
function findBrowser() {
  const candidates = [
    process.env.PUPPETEER_EXECUTABLE_PATH,
    process.env.EDGE_PATH,
    "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
    "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe",
    "/usr/bin/microsoft-edge",
    "/usr/bin/microsoft-edge-stable",
    "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge",
  ].filter(Boolean);

  const browser = candidates.find((candidate) => existsSync(candidate));

  if (!browser) {
    throw new Error(
      "No encontré Edge. Instalalo o pasá la ruta con PUPPETEER_EXECUTABLE_PATH.",
    );
  }

  return browser;
}

// Mermaid dibuja en el cliente. Esperamos a que todos los bloques tengan SVG
// antes de imprimir; si no, el PDF sale con el texto "Cargando diagrama…".
async function waitForDiagrams(page) {
  await page.waitForFunction(
    () => {
      const blocks = [...document.querySelectorAll(".doc-mermaid")];
      return blocks.every((block) => block.querySelector("svg"));
    },
    { timeout: 90000, polling: 400 },
  );

  await page.evaluate(async () => {
    if (document.fonts) {
      await document.fonts.ready;
    }
  });

  // Un respiro extra para que Mermaid termine de aplicar tamaños y estilos.
  await new Promise((resolve) => setTimeout(resolve, 800));
}

async function main() {
  mkdirSync(outputDir, { recursive: true });

  const browser = await puppeteer.launch({
    executablePath: findBrowser(),
    headless: true,
    args: ["--no-sandbox", "--disable-gpu", "--font-render-hinting=none"],
  });

  const host = new URL(baseUrl).hostname;

  // Los PDFs salen en tema oscuro, igual que la web por defecto.
  await browser.setCookie({
    name: "chess-theme",
    value: "dark",
    domain: host,
    path: "/",
  });

  try {
    for (const target of targets) {
      const page = await browser.newPage();
      page.setDefaultNavigationTimeout(120000);
      await page.setViewport({
        width: target.landscape ? 1600 : 1400,
        height: 1000,
        deviceScaleFactor: 1,
      });

      const url = `${baseUrl}${target.url}`;
      console.log(`→ ${url}`);

      await page.goto(url, { waitUntil: "domcontentloaded" });
      await page.waitForSelector(target.ready, { timeout: 90000 });
      await waitForDiagrams(page);

      const filePath = path.join(outputDir, target.file);
      await page.pdf({
        path: filePath,
        format: "A4",
        landscape: Boolean(target.landscape),
        printBackground: true,
        displayHeaderFooter: false,
      });

      console.log(`  ${path.relative(repoRoot, filePath)}`);
      await page.close();
    }
  } finally {
    await browser.close();
  }

  console.log(`\nListo: ${targets.length} PDFs en docs/export/`);
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
