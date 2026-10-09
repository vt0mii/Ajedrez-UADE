package com.uade.ajedrez.domain.service;

import com.uade.ajedrez.domain.model.*;

public class ValidadorMovimiento {

    public ResultadoMovimiento validarPiezaOrigen(Pieza pieza, PartidaAjedrez partida) {
        if (pieza == null) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "No hay una pieza en la posicion de origen");
        }

        if (pieza.getColor() != partida.getTurnoActual()) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "No es el turno de esa pieza");
        }

        return new ResultadoMovimiento(true, null, "Movimiento valido");
    }

    public ResultadoMovimiento validarMovimiento(Tablero tablero, Movimiento movimiento, Color colorJugador) {
        Pieza pieza = tablero.obtenerPieza(movimiento.getOrigen());
        if (pieza == null) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "No hay una pieza en la posicion de origen");
        }

        ResultadoMovimiento resultado = pieza.canMove(tablero, movimiento);
        if (!resultado.isFuePosible()) {
            return resultado;
        }

        if (dejaReyEnJaque(tablero, movimiento, colorJugador)) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "El movimiento deja al rey en jaque");
        }

        return new ResultadoMovimiento(true, null, "Movimiento valido");
    }

    private boolean dejaReyEnJaque(Tablero tablero, Movimiento movimiento, Color colorJugador) {
        Tablero tableroSimulado = simularMovimiento(tablero, movimiento);
        return estaEnJaque(tableroSimulado, colorJugador);
    }

    private Tablero simularMovimiento(Tablero tablero, Movimiento movimiento) {
        Tablero copia = new Tablero();
        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANIO; columna++) {
                Pieza pieza = tablero.obtenerPieza(new Posicion(fila, columna));
                if (pieza != null) {
                    copia.obtenerCasilla(new Posicion(fila, columna)).setPieza(pieza);
                }
            }
        }
        copia.moverPieza(movimiento);
        return copia;
    }

    public boolean estaEnJaque(Tablero tablero, Color colorRey) {
        Posicion posicionRey = encontrarRey(tablero, colorRey);
        if (posicionRey == null) {
            return false;
        }

        Color colorEnemigo = (colorRey == Color.BLANCO) ? Color.NEGRO : Color.BLANCO;

        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANIO; columna++) {
                Pieza pieza = tablero.obtenerPieza(new Posicion(fila, columna));
                if (pieza != null && pieza.getColor() == colorEnemigo) {
                    Movimiento movimientoAtaque = new Movimiento(
                            new Posicion(fila, columna),
                            posicionRey
                    );
                    ResultadoMovimiento resultado = pieza.canMove(tablero, movimientoAtaque);
                    if (resultado.isFuePosible()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean estaEnJaqueMate(Tablero tablero, Color colorJugador) {
        if (!estaEnJaque(tablero, colorJugador)) {
            return false;
        }

        return !tieneMovimientoValido(tablero, colorJugador);
    }

    private boolean tieneMovimientoValido(Tablero tablero, Color colorJugador) {
        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANIO; columna++) {
                Pieza pieza = tablero.obtenerPieza(new Posicion(fila, columna));
                if (pieza != null && pieza.getColor() == colorJugador) {
                    if (piezaPuedeMover(tablero, pieza, new Posicion(fila, columna))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean piezaPuedeMover(Tablero tablero, Pieza pieza, Posicion origen) {
        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANIO; columna++) {
                Posicion destino = new Posicion(fila, columna);
                Movimiento movimiento = new Movimiento(origen, destino);
                ResultadoMovimiento resultado = pieza.canMove(tablero, movimiento);
                if (resultado.isFuePosible()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Posicion encontrarRey(Tablero tablero, Color color) {
        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int columna = 0; columna < Tablero.TAMANIO; columna++) {
                Pieza pieza = tablero.obtenerPieza(new Posicion(fila, columna));
                if (pieza != null && pieza.getTipo() == TipoPieza.REY && pieza.getColor() == color) {
                    return new Posicion(fila, columna);
                }
            }
        }
        return null;
    }
}
