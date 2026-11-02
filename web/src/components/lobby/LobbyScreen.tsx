"use client";

import { useRouter } from "next/navigation";
import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Toast } from "@/components/ui/Toast";
import { api, ApiError, API_URL } from "@/lib/api";
import { isFinishedStatus, STATUS_STYLES } from "@/lib/status";
import type { GameSummary } from "@/lib/types";

// Fecha corta para el listado. Se arma en el cliente porque el formato
// depende del navegador; el backend manda milisegundos desde epoch.
function formatDate(timestamp: number) {
  return new Intl.DateTimeFormat("es-AR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(timestamp));
}

export function LobbyScreen() {
  const router = useRouter();
  const [white, setWhite] = useState("");
  const [black, setBlack] = useState("");
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState<GameSummary[] | null>(null);
  const [loadingSaved, setLoadingSaved] = useState(true);
  const [savedError, setSavedError] = useState<string | null>(null);
  const [openingId, setOpeningId] = useState<string | null>(null);

  useEffect(() => {
    if (!error) {
      return;
    }
    const timer = setTimeout(() => setError(null), 4500);
    return () => clearTimeout(timer);
  }, [error]);

  const loadSaved = useCallback(async () => {
    setLoadingSaved(true);
    try {
      const { games } = await api.listGames();
      setSaved(games);
      setSavedError(null);
    } catch (cause) {
      setSavedError(cause instanceof ApiError ? cause.message : "Error inesperado");
    } finally {
      setLoadingSaved(false);
    }
  }, []);

  useEffect(() => {
    let active = true;

    api
      .listGames()
      .then(({ games }) => {
        if (!active) return;
        setSaved(games);
        setSavedError(null);
      })
      .catch((cause) => {
        if (active) {
          setSavedError(
            cause instanceof ApiError ? cause.message : "Error inesperado",
          );
        }
      })
      .finally(() => {
        if (active) setLoadingSaved(false);
      });

    return () => {
      active = false;
    };
  }, []);

  async function createGame(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setCreating(true);
    setError(null);
    try {
      const game = await api.createGame(white.trim(), black.trim());
      router.push(`/game/${game.id}`);
    } catch (cause) {
      setError(cause instanceof ApiError ? cause.message : "Error inesperado");
      setCreating(false);
    }
  }

  function openSaved(game: GameSummary) {
    setOpeningId(game.id);
    router.push(`/game/${game.id}`);
  }

  return (
    <main className="mx-auto flex w-full max-w-5xl flex-1 flex-col justify-center px-4 py-12 sm:px-6 lg:py-16">
      <div className="text-center">
        <p className="text-[11px] font-medium tracking-[0.2em] text-gold-400 uppercase">
          Ingeniería de Software · UADE
        </p>
        <h1 className="mt-4 font-serif text-5xl leading-[1.03] text-ink-100 sm:text-6xl">
          Ajedrez
        </h1>
        <p className="mt-3 text-sm text-ink-300">
          Medina Cotos · Tortora · Umerez · Quintana
        </p>
        <p className="mx-auto mt-5 max-w-xl text-sm leading-relaxed text-ink-300 sm:text-base">
          Cada jugada la valida el dominio. El frontend solo dibuja el tablero y
          espera la respuesta del servidor: no hay reglas duplicadas en
          TypeScript.
        </p>
      </div>

      <form
        onSubmit={createGame}
        className="mt-10 rounded-2xl border border-ink-800 bg-ink-900/60 p-6"
      >
        <h2 className="text-sm font-semibold tracking-tight text-ink-100">
          Nueva partida
        </h2>
        <p className="mt-1 text-xs text-ink-400">
          Elegí los nombres o dejá que el servidor los complete.
        </p>

        <div className="mt-5 grid gap-4 sm:grid-cols-2">
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

        <Button type="submit" className="mt-5 w-full" disabled={creating}>
          {creating ? "Creando…" : "Empezar partida"}
        </Button>
      </form>

      <section className="mt-10 rounded-2xl border border-ink-800 bg-ink-900/60 p-6">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h2 className="text-sm font-semibold tracking-tight text-ink-100">
              Partidas guardadas
            </h2>
            <p className="mt-1 text-xs text-ink-400">
              Se guardan solas después de cada jugada. Elegí una para seguir.
            </p>
          </div>
          <Button
            variant="ghost"
            onClick={() => void loadSaved()}
            disabled={loadingSaved}
          >
            {loadingSaved ? "Actualizando…" : "Actualizar"}
          </Button>
        </div>

        {loadingSaved && !saved ? (
          <p className="mt-5 text-sm text-ink-400">Buscando partidas…</p>
        ) : null}

        {savedError ? (
          <p className="mt-5 text-sm text-danger">
            No pudimos leer las partidas guardadas. {savedError}
          </p>
        ) : null}

        {!loadingSaved && !savedError && saved?.length === 0 ? (
          <p className="mt-5 text-sm text-ink-400">
            Todavía no hay partidas guardadas. Empezá una nueva y aparecerá acá.
          </p>
        ) : null}

        {saved && saved.length > 0 ? (
          <ul className="mt-4 divide-y divide-ink-800">
            {saved.map((game) => {
              const status = STATUS_STYLES[game.status];
              const moves = game.moveCount === 1 ? "1 jugada" : `${game.moveCount} jugadas`;

              return (
                <li key={game.id} className="flex items-center gap-2">
                  <button
                    type="button"
                    onClick={() => openSaved(game)}
                    className="group flex min-w-0 flex-1 flex-col gap-2 py-3 text-left transition-colors hover:bg-ink-800/40 sm:flex-row sm:items-center sm:justify-between sm:px-2"
                  >
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <p className="truncate text-sm text-ink-100">
                          {game.whitePlayer}{" "}
                          <span className="text-ink-400">vs</span>{" "}
                          {game.blackPlayer}
                        </p>
                        <span
                          className={`rounded-full px-2 py-0.5 text-[10px] font-semibold tracking-[0.1em] uppercase ring-1 ring-inset ${status.className}`}
                        >
                          {status.label}
                        </span>
                      </div>
                      <p className="mt-1 text-[11px] text-ink-400">
                        Turno {game.turnNumber} · {moves}
                        {game.lastMove
                          ? ` · Última: ${game.lastMove.from}–${game.lastMove.to}`
                          : ""}
                      </p>
                    </div>

                    <div className="flex items-center gap-3 text-[11px] text-ink-400">
                      <span>{formatDate(game.updatedAt)}</span>
                      <span className="font-medium text-gold-300 transition-colors group-hover:text-gold-400">
                        {openingId === game.id ? "Abriendo…" : "Abrir →"}
                      </span>
                    </div>
                  </button>
                  {isFinishedStatus(game.status) ? (
                    <Link
                      href={`/game/${game.id}/replay`}
                      className="shrink-0 rounded-lg border border-ink-700 px-2.5 py-1.5 text-[11px] font-medium text-ink-300 transition-colors hover:border-gold-400/40 hover:text-gold-300"
                    >
                      Replay
                    </Link>
                  ) : null}
                </li>
              );
            })}
          </ul>
        ) : null}
      </section>

      <p className="mt-8 text-center text-xs text-ink-400">
        Backend: <span className="font-mono text-ink-300">{API_URL}</span>
      </p>

      {error ? <Toast message={error} /> : null}
    </main>
  );
}
