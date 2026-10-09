package com.uade.ajedrez.domain.model;

public class Casilla {
    private final Posicion posicion;
    private Pieza pieza;

    public Casilla(Posicion posicion) {
        this.posicion = posicion;
        this.pieza = null;
    }

    public Pieza getPieza() {
        return pieza;
    }

    public void setPieza(Pieza pieza) {
        this.pieza = pieza;
    }

    public Posicion getPosicion() {
        return posicion;
    }
}
