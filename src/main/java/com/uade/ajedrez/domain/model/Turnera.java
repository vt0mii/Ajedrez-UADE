package com.uade.ajedrez.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Turnera {
    private final List<Jugador> jugadores;
    private int indiceActual;
    private int turnoNumero;

    public Turnera(Jugador... jugadores) {
        if (jugadores == null || jugadores.length < 2) {
            throw new IllegalArgumentException("Se necesitan 2 jugadores");
        }
        this.jugadores = new ArrayList<>();
        for (Jugador j : jugadores) {
            this.jugadores.add(j);
        }
        this.indiceActual = 0;
        this.turnoNumero = 1;
    }

    public Jugador getJugadorActual() {
        return jugadores.get(indiceActual);
    }

    public boolean esTurnoDe(Jugador jugador) {
        return getJugadorActual().equals(jugador);
    }

    public Jugador siguienteTurno() {
        indiceActual = 1 - indiceActual;
        if (indiceActual == 0) {
            turnoNumero++;
        }
        return getJugadorActual();
    }

    public int getTurnoNumero() {
        return turnoNumero;
    }

    public List<Jugador> getJugadores() {
        return this.jugadores;
    }
}
