import React, { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import * as api from './api.js';
import './styles.css';

const symbols = {
  WHITE: { KING: '♔', QUEEN: '♕', ROOK: '♖', BISHOP: '♗', KNIGHT: '♘', PAWN: '♙' },
  BLACK: { KING: '♚', QUEEN: '♛', ROOK: '♜', BISHOP: '♝', KNIGHT: '♞', PAWN: '♟' },
};
const names = { KING: 'rey', QUEEN: 'reina', ROOK: 'torre', BISHOP: 'alfil', KNIGHT: 'caballo', PAWN: 'peón' };
const side = (color) => color === 'WHITE' ? 'Blancas' : 'Negras';
const coordinate = (row, column) => `${'abcdefgh'[column]}${8 - row}`;

function App() {
  const [game, setGame] = useState(null);
  const [selected, setSelected] = useState(null);
  const [busy, setBusy] = useState(true);
  const [notice, setNotice] = useState('');
  const [error, setError] = useState('');
  const [history, setHistory] = useState([]);
  const [lastMove, setLastMove] = useState([]);
  const [confirmReset, setConfirmReset] = useState(false);

  async function refresh() {
    setBusy(true);
    setError('');
    try {
      setGame(await api.getGame());
      setSelected(null);
      setHistory([]);
      setLastMove([]);
      setNotice('Tablero actualizado. Elegí una pieza para comenzar.');
    } catch (e) { setError(e.message); }
    finally { setBusy(false); }
  }
  useEffect(() => { refresh(); }, []);

  async function choose(row, column) {
    if (busy || !game || game.state === 'CHECKMATE') return;
    const square = coordinate(row, column);
    const piece = game.pieces.find(p => p.row === row && p.column === column);
    if (selected === square) { setSelected(null); return; }
    if (piece?.color === game.currentTurn) {
      setSelected(square);
      setNotice(`Pieza en ${square}. Elegí la casilla de destino.`);
      setError('');
      return;
    }
    if (!selected) { setNotice('Elegí una pieza del color que tiene el turno.'); return; }
    setBusy(true);
    setError('');
    try {
      const result = await api.move(selected, square);
      setGame(result.game);
      if (result.ok) {
        setHistory(items => [...items, { color: game.currentTurn, origin: selected, destination: square }]);
        setLastMove([selected, square]);
        setSelected(null);
        setNotice(`Movimiento ${selected} → ${square} realizado.`);
      } else {
        setError(`Jugada rechazada por Java: ${result.message}`);
        setSelected(null);
      }
    } catch (e) { setError(e.message); setSelected(null); }
    finally { setBusy(false); }
  }

  async function reset() {
    setBusy(true);
    setError('');
    try {
      setGame(await api.newGame());
      setSelected(null);
      setHistory([]);
      setLastMove([]);
      setConfirmReset(false);
      setNotice('Nueva partida. Empiezan las blancas.');
    } catch (e) { setError(e.message); }
    finally { setBusy(false); }
  }

  const ended = game?.state === 'CHECKMATE';
  return <div className="app">
    <header className="header">
      <a className="brand" href="/" aria-label="Ajedrez UADE, inicio"><span className="brand-mark">♞</span><span>AJEDREZ <b>UADE</b></span></a>
      <span className="course">INGENIERÍA DE SOFTWARE <span> / </span> TRABAJO PRÁCTICO</span>
    </header>
    <main>
      <div className="intro"><div><p className="eyebrow">PENSÁ TU PRÓXIMA JUGADA</p><h1>La partida empieza acá.</h1><p className="subtitle">Dos colores. Un tablero. Una nueva estrategia.</p></div><span className="local-tag"><i /> PARTIDA LOCAL · 2 JUGADORES</span></div>
      <div className="game-layout">
        <section className="board-panel" aria-label="Mesa de juego">
          <div className="player"><span className="avatar black-avatar">♚</span><div><strong>Negras</strong><small>Jugador 02</small></div>{game?.currentTurn === 'BLACK' && !ended && <span className="turn-pill">Su turno</span>}</div>
          <div className="board-frame"><div className="board" aria-label="Tablero de ajedrez" aria-busy={busy}>
            {Array.from({ length: 64 }, (_, i) => {
              const row = Math.floor(i / 8), column = i % 8, square = coordinate(row, column);
              const piece = game?.pieces.find(p => p.row === row && p.column === column);
              return <button key={square} type="button" disabled={busy || !game || ended}
                className={`square ${(row + column) % 2 ? 'dark' : 'light'} ${selected === square ? 'selected' : ''} ${lastMove.includes(square) ? 'last-move' : ''}`}
                onClick={() => choose(row, column)} aria-pressed={selected === square}
                aria-label={`${square}${piece ? `, ${names[piece.type]} ${piece.color === 'WHITE' ? 'blanco' : 'negro'}` : ', vacía'}`}>
                {column === 0 && <span className="rank">{8 - row}</span>}
                {piece && <span aria-hidden="true" className={`piece ${piece.color.toLowerCase()}`}>{symbols[piece.color][piece.type]}</span>}
                {row === 7 && <span className="file">{'abcdefgh'[column]}</span>}
              </button>;
            })}
          </div></div>
          <div className="player"><span className="avatar white-avatar">♔</span><div><strong>Blancas</strong><small>Jugador 01</small></div>{game?.currentTurn === 'WHITE' && !ended && <span className="turn-pill">Su turno</span>}</div>
          <p className="board-caption">Seleccioná una pieza y después su destino. Java valida cada jugada.</p>
        </section>
        <aside>
          <section className="status-card">
            <p className="eyebrow">EN EL TABLERO</p>
            <div className="status-title"><span className="status-dot" /><h2>{!game ? 'Conectando…' : ended ? 'Jaque mate' : `Turno de ${side(game.currentTurn).toLowerCase()}`}</h2></div>
            <p className="status-detail">{ended ? `Ganan las ${side(game.currentTurn === 'WHITE' ? 'BLACK' : 'WHITE').toLowerCase()}.` : game?.state === 'CHECK' ? '¡Jaque! Protegé tu rey para continuar.' : 'El próximo movimiento está en tus manos.'}</p>
            <div className="stats"><div><span>RONDA</span><strong>{game?.turnNumber ?? '—'}</strong></div><div><span>ESTADO</span><strong className="state-text">{!game ? 'Sin conexión' : ended ? 'Finalizada' : game.state === 'CHECK' ? 'Jaque' : 'En juego'}</strong></div></div>
            <div className="feedback" aria-live="polite" role="status">{busy ? 'Consultando el tablero…' : error || notice}</div>
            {error && <span className="error-label">Revisá el mensaje antes de continuar.</span>}
            <button className="primary-button" onClick={() => setConfirmReset(true)} disabled={busy || !game}>Nueva partida <span>↗</span></button>
            {confirmReset && <div className="reset-confirm"><p>¿Reiniciar la partida compartida? Se perderán los movimientos actuales.</p><button onClick={reset} disabled={busy}>Sí, reiniciar</button><button onClick={() => setConfirmReset(false)} disabled={busy}>Cancelar</button></div>}
            <button className="text-button" onClick={refresh} disabled={busy}>↻ Actualizar tablero</button>
          </section>
          <section className="history-card"><div className="section-heading"><h2>Movimientos</h2><span>{history.length.toString().padStart(2, '0')}</span></div>
            {history.length === 0 ? <div className="empty-history"><span>♙</span><p>Todo está por jugarse.</p><small>Las jugadas de esta sesión aparecerán acá.</small></div> : <ol className="history">{history.map((item, i) => <li key={i}><span className="move-number">{i + 1}.</span><span>{item.color === 'WHITE' ? '♙' : '♟'} {side(item.color)}</span><strong>{item.origin} <span>→</span> {item.destination}</strong></li>)}</ol>}
          </section>
          <p className="session-note">Una partida compartida en memoria. Al reiniciar Java, el tablero vuelve a su posición inicial.</p>
        </aside>
      </div>
    </main>
    <footer><span>UADE <b>·</b> AJEDREZ</span><span>Hecho para aprender. Pensado para crecer.</span></footer>
  </div>;
}

createRoot(document.getElementById('root')).render(<App />);
