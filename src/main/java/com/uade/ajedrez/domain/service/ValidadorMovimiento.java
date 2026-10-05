package com.uade.ajedrez.domain.service;

public class ValidadorMovimiento {
    public ResultadoMovimiento validarPiezaOrigen(Pieza pieza, PartidaAjedrez partida) {
        if (pieza == null) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "No hay una pieza en la posicion de origen");
        }

        if (pieza.getColor() != partida.getTurnoActual()) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "No es el turno de esa pieza");
        }

        return new ResultadoMovimiento(true, null, "Movimiento valido");
    }
}