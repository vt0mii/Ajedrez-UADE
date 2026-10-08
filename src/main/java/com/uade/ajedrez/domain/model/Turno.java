package com.uade.ajedrez.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Turno {
    private final List<Jugador> jugadores;
    private int indiceActual;
    private int turnoNumero;

    public Turno(Jugador jugador1, Jugador jugador2) {
        if (jugador1 == null || jugador2 == null) {
            throw new IllegalArgumentException("Un jugador no es valido");
        }
        this.jugadores = new ArrayList<>();
        Collections.addAll(this.jugadores, jugador1, jugador2);
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
