"use client";

import { useCallback, useEffect, useState } from "react";
import { api, ApiError } from "@/lib/api";
import { isPromotionMove, pieceAt } from "@/lib/board";
import { isFinishedStatus } from "@/lib/status";
import type { Color, GameView, PieceType } from "@/lib/types";

const POLL_INTERVAL_MS = 2000;
const NOTICE_DURATION_MS = 4500;

export type Notice = {
  message: string;
  tone: "error" | "info";
};

// Jugada que espera que el jugador elija la pieza de promocion.
export type PendingPromotion = {
  from: string;
  to: string;
  color: Color;
};

function messageOf(error: unknown): string {
  return error instanceof ApiError ? error.message : "Error inesperado";
}

// Estado de una partida: carga inicial, sincronización y acciones. Toda la
// validación está en el backend; acá solo se maneja la selección visual.
export function useGame(gameId: string) {
  const [game, setGame] = useState<GameView | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selected, setSelected] = useState<string | null>(null);
  const [legalMoves, setLegalMoves] = useState<string[]>([]);
  const [busy, setBusy] = useState(false);
  const [saving, setSaving] = useState(false);
  const [notice, setNotice] = useState<Notice | null>(null);
  const [pendingPromotion, setPendingPromotion] = useState<PendingPromotion | null>(null);

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
          // Si falla el polling, no cortamos la partida.
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

  async function play(from: string, to: string, promotion?: PieceType) {
    setBusy(true);
    try {
      setGame(await api.makeMove(gameId, from, to, promotion));
      clearSelection();
      setPendingPromotion(null);
    } catch (cause) {
      notify(messageOf(cause));
    } finally {
      setBusy(false);
    }
  }

  async function selectSquare(square: string) {
    if (!game || busy || isFinishedStatus(game.status)) {
      return;
    }

    if (selected && legalMoves.includes(square)) {
      const piece = pieceAt(game.board, selected);
      if (piece && isPromotionMove(piece, square)) {
        setPendingPromotion({ from: selected, to: square, color: piece.color });
        return;
      }
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

  const cancelPromotion = useCallback(() => {
    setPendingPromotion(null);
  }, []);

  async function choosePromotion(piece: PieceType) {
    if (!pendingPromotion) {
      return;
    }
    await play(pendingPromotion.from, pendingPromotion.to, piece);
  }

  async function abandon() {
    setBusy(true);
    try {
      setGame(await api.abandonGame(gameId));
      clearSelection();
      setPendingPromotion(null);
    } catch (cause) {
      notify(messageOf(cause));
    } finally {
      setBusy(false);
    }
  }

  // Guardado manual. El backend ya guarda después de cada jugada; esto solo
  // confirma que el archivo quedó actualizado.
  async function save() {
    setSaving(true);
    try {
      setGame(await api.saveGame(gameId));
      notify("Partida guardada.", "info");
    } catch (cause) {
      notify(messageOf(cause));
    } finally {
      setSaving(false);
    }
  }

  return {
    game,
    loading,
    error,
    selected,
    legalMoves,
    busy,
    saving,
    notice,
    pendingPromotion,
    dismissNotice: () => setNotice(null),
    notify,
    selectSquare,
    choosePromotion,
    cancelPromotion,
    clearSelection,
    abandon,
    save,
  };
}
