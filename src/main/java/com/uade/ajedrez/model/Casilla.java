package com.uade.ajedrez.model;

public class Casilla {
    private Pieza pieza;
    private Posicion posicion;

    public Casilla(Pieza pieza, Posicion posicion) {
        this.pieza = pieza;
        this.posicion = posicion;
    }

    public Casilla(Posicion posicion) {
        this.pieza = null;
        this.posicion = posicion;
    }

    public Pieza getPieza() {
        return this.pieza;
    }

    public void setPieza(Pieza pieza) {
        this.pieza = pieza;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    
}
