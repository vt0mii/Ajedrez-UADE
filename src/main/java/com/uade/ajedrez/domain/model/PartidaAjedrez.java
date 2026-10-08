package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.service.ValidadorMovimiento;

public class PartidaAjedrez {
    private final Tablero tablero;
    private final ValidadorMovimiento validadorMovimiento;
    private final Turno turno;
    private Estado estado;

    public PartidaAjedrez(Jugador jugadorBlanco, Jugador jugadorNegro, Tablero tablero,
            ValidadorMovimiento validadorMovimiento) {
        this.tablero = tablero;
        this.validadorMovimiento = validadorMovimiento;
        this.turno = new Turno(jugadorBlanco, jugadorNegro);
        this.estado = Estado.EN_CURSO;
    }

    public ResultadoMovimiento realizarMovimiento(Movimiento movimiento) {
        if (estado == Estado.JAQUE_MATE || estado == Estado.TABLAS) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "La partida ya finalizo");
        }

        Pieza pieza = tablero.obtenerPieza(movimiento.getOrigen());
        ResultadoMovimiento validacion = validadorMovimiento.validarPiezaOrigen(pieza, this);
        if (!validacion.isFuePosible()) {
            return validacion;
        }

        ResultadoMovimiento resultado = pieza.canMove(tablero, movimiento);
        if (!resultado.isFuePosible()) {
            return resultado;
        }

        tablero.moverPieza(movimiento);
        turno.siguienteTurno();

        return resultado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Color getTurnoActual() {
        return turno.getJugadorActual().getColor();
    }

    public Jugador getJugadorEnTurno() {
        return turno.getJugadorActual();
    }

    public Turno getTurno() {
        return turno;
    }

    public Tablero getTablero() {
        return tablero;
    }
}
