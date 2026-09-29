package com.uade.ajedrez.domain.model;

public class Movimiento {
    private Posicion origen;
    private Posicion destino;

    public Movimiento(Posicion o, Posicion d) {
        this.origen = o;
        this.destino = d;
    }

    public Posicion getOrigen() {
        return this.origen;
    }

    public Posicion getDestino() {
        return this.destino;
    }
}
