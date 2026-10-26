"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Toast } from "@/components/ui/Toast";
import { api, ApiError, API_URL } from "@/lib/api";

type Pending = "create" | "resume" | null;

export function LobbyScreen() {
  const router = useRouter();
  const [white, setWhite] = useState("");
  const [black, setBlack] = useState("");
  const [gameId, setGameId] = useState("");
  const [pending, setPending] = useState<Pending>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!error) {
      return;
    }
    const timer = setTimeout(() => setError(null), 4500);
    return () => clearTimeout(timer);
  }, [error]);

  async function createGame(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setPending("create");
    setError(null);
    try {
      const game = await api.createGame(white.trim(), black.trim());
      router.push(`/game/${game.id}`);
    } catch (cause) {
      setError(cause instanceof ApiError ? cause.message : "Error inesperado");
      setPending(null);
    }
  }

  async function resumeGame(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const id = gameId.trim();
    if (!id) {
      return;
    }

    setPending("resume");
    setError(null);
    try {
      const game = await api.getGame(id);
      router.push(`/game/${game.id}`);
    } catch (cause) {
      setError(cause instanceof ApiError ? cause.message : "Error inesperado");
      setPending(null);
    }
  }

  return (
    <main className="mx-auto flex w-full max-w-5xl flex-1 flex-col justify-center px-4 py-12 sm:px-6 lg:py-16">
      <div className="max-w-2xl">
        <p className="text-[11px] font-medium tracking-[0.2em] text-gold-400 uppercase">
          Ingeniería de Software · UADE
        </p>
        <h1 className="mt-4 font-serif text-5xl leading-[1.03] text-ink-100 sm:text-6xl">
          El clásico, con las reglas en Java y el tablero en la web.
        </h1>
        <p className="mt-5 max-w-xl text-sm leading-relaxed text-ink-300 sm:text-base">
          Cada jugada la valida el dominio. El frontend solo dibuja el tablero y
          espera la respuesta del servidor: no hay reglas duplicadas en
          TypeScript.
        </p>
      </div>

      <div className="mt-10 grid gap-5 md:grid-cols-2">
        <form
          onSubmit={createGame}
          className="rounded-2xl border border-ink-800 bg-ink-900/60 p-6"
        >
          <h2 className="text-sm font-semibold tracking-tight text-ink-100">
            Nueva partida
          </h2>
          <p className="mt-1 text-xs text-ink-400">
            Elegí los nombres o dejá que el servidor los complete.
          </p>

          <div className="mt-5 space-y-4">
            <Input
              id="white-player"
              label="Blancas"
              placeholder="White"
              maxLength={40}
              value={white}
              onChange={(event) => setWhite(event.target.value)}
              autoComplete="off"
            />
            <Input
              id="black-player"
              label="Negras"
              placeholder="Black"
              maxLength={40}
              value={black}
              onChange={(event) => setBlack(event.target.value)}
              autoComplete="off"
            />
          </div>

          <Button type="submit" className="mt-5 w-full" disabled={pending !== null}>
            {pending === "create" ? "Creando…" : "Empezar partida"}
          </Button>
        </form>

        <form
          onSubmit={resumeGame}
          className="flex flex-col rounded-2xl border border-ink-800 bg-ink-900/60 p-6"
        >
          <h2 className="text-sm font-semibold tracking-tight text-ink-100">
            Retomar partida
          </h2>
          <p className="mt-1 text-xs text-ink-400">
            Pegá el id que aparece en la URL o en el historial.
          </p>

          <div className="mt-5 flex-1 space-y-4">
            <Input
              id="game-id"
              label="ID de partida"
              placeholder="g_..."
              value={gameId}
              onChange={(event) => setGameId(event.target.value)}
              className="font-mono"
              autoComplete="off"
              spellCheck={false}
              hint="La partida se guarda sola después de cada jugada."
            />
          </div>

          <Button
            type="submit"
            variant="ghost"
            className="mt-5 w-full"
            disabled={pending !== null || gameId.trim().length === 0}
          >
            {pending === "resume" ? "Abriendo…" : "Abrir partida"}
          </Button>
        </form>
      </div>

      <p className="mt-8 text-xs text-ink-400">
        Backend: <span className="font-mono text-ink-300">{API_URL}</span>
      </p>

      {error ? <Toast message={error} /> : null}
    </main>
  );
}
