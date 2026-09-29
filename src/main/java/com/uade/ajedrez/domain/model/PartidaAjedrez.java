package com.uade.ajedrez.domain.model;

public class PartidaAjedrez {
    private final Jugador jugadorBlanco;
    private final Jugador jugadorNegro;
    private final Tablero tablero;
    private Estado estado;
    private Color turnoActual;

    public PartidaAjedrez(Jugador jugadorBlanco, Jugador jugadorNegro, Tablero tablero) {
        this.jugadorBlanco = jugadorBlanco;
        this.jugadorNegro = jugadorNegro;
        this.tablero = tablero;
        this.estado = Estado.EN_CURSO;
        this.turnoActual = Color.BLANCO;
    }

    public ResultadoMovimiento realizarMovimiento(Movimiento movimiento) {
        return  null;
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
