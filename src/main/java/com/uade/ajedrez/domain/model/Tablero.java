package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.factory.PiezaFactory;

public class Tablero {
    public static final int TAMANIO = 8;

    private final Casilla[][] casillas = new Casilla[TAMANIO][TAMANIO];
    private Color perspectiva = Color.BLANCO;

    public Tablero() {
        generarTablero();
    }

    public Tablero(Color perspectiva) {
        this.perspectiva = perspectiva;
        generarTablero();
    }

    public void setPerspectiva(Color perspectiva) {
        this.perspectiva = perspectiva;
    }

    public Color getPerspectiva() {
        return perspectiva;
    }

    public void generarTablero() {
        for (int fila = 0; fila < TAMANIO; fila++) {
            for (int columna = 0; columna < TAMANIO; columna++) {
                casillas[fila][columna] = new Casilla(new Posicion(fila, columna));
            }
        }

        TipoPieza[] ordenInicial = {
            TipoPieza.TORRE,
            TipoPieza.CABALLO,
            TipoPieza.ALFIL,
            TipoPieza.REINA,
            TipoPieza.REY,
            TipoPieza.ALFIL,
            TipoPieza.CABALLO,
            TipoPieza.TORRE
        };

        for (int columna = 0; columna < TAMANIO; columna++) {
            colocarPieza(0, columna, Color.NEGRO, ordenInicial[columna]);
            colocarPieza(1, columna, Color.NEGRO, TipoPieza.PEON);
            colocarPieza(6, columna, Color.BLANCO, TipoPieza.PEON);
            colocarPieza(7, columna, Color.BLANCO, ordenInicial[columna]);
        }
    }

    public Casilla obtenerCasilla(Posicion posicion) {
        validarPosicion(posicion);
        return casillas[posicion.getFila()][posicion.getColumna()];
    }

    public Pieza obtenerPieza(Posicion posicion) {
        return obtenerCasilla(posicion).getPieza();
    }

    @Override
    public String toString() {
        StringBuilder tablero = new StringBuilder();
        
        if (perspectiva == Color.BLANCO) {
            tablero.append("  a b c d e f g h\n");
            for (int fila = 0; fila < TAMANIO; fila++) {
                tablero.append(TAMANIO - fila).append(' ');
                for (int columna = 0; columna < TAMANIO; columna++) {
                    Pieza pieza = casillas[fila][columna].getPieza();
                    tablero.append(pieza == null ? '.' : simboloPieza(pieza));
                    if (columna < TAMANIO - 1) {
                        tablero.append(' ');
                    }
                }
                tablero.append(' ').append(TAMANIO - fila).append('\n');
            }
            tablero.append("  a b c d e f g h");
        } else {
            tablero.append("  h g f e d c b a\n");
            for (int fila = TAMANIO - 1; fila >= 0; fila--) {
                tablero.append(TAMANIO - fila).append(' ');
                for (int columna = TAMANIO - 1; columna >= 0; columna--) {
                    Pieza pieza = casillas[fila][columna].getPieza();
                    tablero.append(pieza == null ? '.' : simboloPieza(pieza));
                    if (columna > 0) {
                        tablero.append(' ');
                    }
                }
                tablero.append(' ').append(TAMANIO - fila).append('\n');
            }
            tablero.append("  h g f e d c b a");
        }
        
        return tablero.toString();
    }

    private void colocarPieza(int fila, int columna, Color color, TipoPieza tipo) {
        casillas[fila][columna].setPieza(PiezaFactory.crear(tipo, color));
    }

    private char simboloPieza(Pieza pieza) {
        char simbolo = pieza.getTipo().name().charAt(0);
        if (pieza.getTipo() == TipoPieza.REINA) {
            simbolo = 'Q';
        }
        return pieza.getColor() == Color.NEGRO ? Character.toLowerCase(simbolo) : simbolo;
    }

    private void validarPosicion(Posicion posicion) {
        if (posicion == null
                || posicion.getFila() < 0 || posicion.getFila() >= TAMANIO
                || posicion.getColumna() < 0 || posicion.getColumna() >= TAMANIO) {
            throw new IllegalArgumentException("La posición debe estar dentro del tablero (0-7).");
        }
    }
    public void moverPieza(Movimiento movimiento) {
        Casilla origen = obtenerCasilla(movimiento.getOrigen());
        Casilla destino = obtenerCasilla(movimiento.getDestino());
        destino.setPieza(origen.getPieza());
        origen.setPieza(null);
    }
}
