export async function request(path, options = {}) {
  let response;
  try {
    response = await fetch(`/api${path}`, { ...options, signal: AbortSignal.timeout(15000) });
  } catch {
    throw new Error('No se pudo conectar con Java. Verificá que la API esté ejecutándose en el puerto 8080 y recargá el tablero.');
  }
  let data;
  try {
    data = await response.json();
  } catch {
    throw new Error('La API no está disponible. Verificá el servidor Java y recargá el tablero.');
  }
  // Rejected moves include the authoritative board so the UI can resynchronize.
  if (!response.ok && response.status !== 422) throw new Error(data.message || 'Error del servidor.');
  return { ...data, ok: response.ok };
}

export const getGame = () => request('/game');
export const newGame = () => request('/game/new', { method: 'POST' });
export const move = (origin, destination) => request('/moves', {
  method: 'POST',
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  body: new URLSearchParams({ origin, destination }),
});
