package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.service.ValidadorMovimiento;

public class PartidaAjedrez {
    @SuppressWarnings("unused")
    private final Jugador jugadorBlanco;
    @SuppressWarnings("unused")
    private final Jugador jugadorNegro;
    private final Tablero tablero;
    private final ValidadorMovimiento validadorMovimiento;
    private Estado estado;
    private Color turnoActual;

    public PartidaAjedrez(Jugador jugadorBlanco, Jugador jugadorNegro, Tablero tablero, ValidadorMovimiento validadorMovimiento) {
        this.jugadorBlanco = jugadorBlanco;
        this.jugadorNegro = jugadorNegro;
        this.tablero = tablero;
        this.validadorMovimiento = validadorMovimiento;
        this.estado = Estado.EN_CURSO;
        this.turnoActual = Color.BLANCO;
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
        setTurnoActual(turnoActual == Color.BLANCO ? Color.NEGRO : Color.BLANCO);

        return resultado;
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
