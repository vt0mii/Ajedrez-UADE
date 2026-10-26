const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

/** Error con el código HTTP y el código de error que devuelve el backend. */
export class ApiError extends Error {
  readonly status: number;
  readonly code: string;

  constructor(status: number, code: string, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}`, {
      ...init,
      headers: { "Content-Type": "application/json", ...init?.headers },
      cache: "no-store",
    });
  } catch {
    throw new ApiError(
      0,
      "NETWORK_ERROR",
      `No se pudo conectar con el servidor en ${API_URL}`,
    );
  }

  if (!response.ok) {
    const body = await response.json().catch(() => null);
    throw new ApiError(
      response.status,
      body?.error ?? "UNKNOWN",
      body?.message ?? `Error ${response.status}`,
    );
  }

  return (await response.json()) as T;
}

export const api = {
  createGame: (whitePlayerName: string, blackPlayerName: string) =>
    request<import("./types").GameView>("/api/games", {
      method: "POST",
      body: JSON.stringify({ whitePlayerName, blackPlayerName }),
    }),

  getGame: (gameId: string) =>
    request<import("./types").GameView>(`/api/games/${gameId}`),

  legalMoves: (gameId: string, from: string) =>
    request<{ moves: string[] }>(
      `/api/games/${gameId}/legal-moves?from=${encodeURIComponent(from)}`,
    ),

  makeMove: (gameId: string, from: string, to: string) =>
    request<import("./types").GameView>(`/api/games/${gameId}/moves`, {
      method: "POST",
      body: JSON.stringify({ from, to }),
    }),

  abandonGame: (gameId: string) =>
    request<import("./types").GameView>(`/api/games/${gameId}/abandon`, {
      method: "POST",
    }),
};

export { API_URL };
