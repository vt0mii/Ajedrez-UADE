package com.uade.ajedrez.model;

public class PartidaAjedrez {
    private Jugador jugadorBlanco;
    private Jugador jugadorNegro;
    private Estado estado;
    private Color turnoActual;

    public PartidaAjedrez(Jugador jugadorBlanco, Jugador jugadorNegro, Estado estado) {
        this.jugadorBlanco = jugadorBlanco;
        this.jugadorNegro = jugadorNegro;
        this.estado = Estado.EN_CURSO;
        this.turnoActual = Color.BLANCO;
    }

    public ResultadoMovimiento realizarMovimiento(Movimiento movimiento) {
        return null;
    }

    public Jugador getJugadorBlanco() {
        return jugadorBlanco;
    }

    public void setJugadorBlanco(Jugador jugadorBlanco) {
        this.jugadorBlanco = jugadorBlanco;
    }

    public Jugador getJugadorNegro() {
        return jugadorNegro;
    }

    public void setJugadorNegro(Jugador jugadorNegro) {
        this.jugadorNegro = jugadorNegro;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Color getTurnoActual() {
        return turnoActual;
    }

    public void setTurnoActual(Color turnoActual) {
        this.turnoActual = turnoActual;
    }

}
