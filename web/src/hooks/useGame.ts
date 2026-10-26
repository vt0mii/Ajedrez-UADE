"use client";

import { useCallback, useEffect, useState } from "react";
import { api, ApiError } from "@/lib/api";
import { pieceAt } from "@/lib/board";
import type { GameView } from "@/lib/types";

const POLL_INTERVAL_MS = 2000;
const NOTICE_DURATION_MS = 4500;

export type Notice = {
  message: string;
  tone: "error" | "info";
};

function messageOf(error: unknown): string {
  return error instanceof ApiError ? error.message : "Error inesperado";
}

/**
 * Estado de una partida: carga inicial, sincronización y acciones. Toda la
 * validación vive en el backend; acá solo se maneja la selección visual.
 */
export function useGame(gameId: string) {
  const [game, setGame] = useState<GameView | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selected, setSelected] = useState<string | null>(null);
  const [legalMoves, setLegalMoves] = useState<string[]>([]);
  const [busy, setBusy] = useState(false);
  const [notice, setNotice] = useState<Notice | null>(null);

  const clearSelection = useCallback(() => {
    setSelected(null);
    setLegalMoves([]);
  }, []);

  const notify = useCallback((message: string, tone: Notice["tone"] = "error") => {
    setNotice({ message, tone });
  }, []);

  useEffect(() => {
    let active = true;

    api
      .getGame(gameId)
      .then((view) => {
        if (active) setGame(view);
      })
      .catch((cause) => {
        if (active) setError(messageOf(cause));
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [gameId]);

  useEffect(() => {
    const timer = setInterval(() => {
      if (document.visibilityState !== "visible") {
        return;
      }
      api
        .getGame(gameId)
        .then(setGame)
        .catch(() => {
          // Un fallo de polling no debe interrumpir la partida en curso.
        });
    }, POLL_INTERVAL_MS);

    return () => clearInterval(timer);
  }, [gameId]);

  useEffect(() => {
    if (!notice) {
      return;
    }
    const timer = setTimeout(() => setNotice(null), NOTICE_DURATION_MS);
    return () => clearTimeout(timer);
  }, [notice]);

  async function play(from: string, to: string) {
    setBusy(true);
    try {
      setGame(await api.makeMove(gameId, from, to));
      clearSelection();
    } catch (cause) {
      notify(messageOf(cause));
    } finally {
      setBusy(false);
    }
  }

  async function selectSquare(square: string) {
    if (!game || busy || game.status === "CHECKMATE" || game.status === "ABANDONED") {
      return;
    }

    if (selected && legalMoves.includes(square)) {
      await play(selected, square);
      return;
    }

    const piece = pieceAt(game.board, square);
    if (!piece || piece.color !== game.turn) {
      clearSelection();
      return;
    }

    if (selected === square) {
      clearSelection();
      return;
    }

    setSelected(square);
    setLegalMoves([]);
    try {
      const result = await api.legalMoves(game.id, square);
      setLegalMoves(result.moves);
    } catch (cause) {
      clearSelection();
      notify(messageOf(cause));
    }
  }

  async function abandon() {
    setBusy(true);
    try {
      setGame(await api.abandonGame(gameId));
      clearSelection();
    } catch (cause) {
      notify(messageOf(cause));
    } finally {
      setBusy(false);
    }
  }

  return {
    game,
    loading,
    error,
    selected,
    legalMoves,
    busy,
    notice,
    dismissNotice: () => setNotice(null),
    notify,
    selectSquare,
    clearSelection,
    abandon,
  };
}
