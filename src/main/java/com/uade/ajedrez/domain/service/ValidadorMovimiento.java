package com.uade.ajedrez.domain.service;

import com.uade.ajedrez.domain.model.Motivo;
import com.uade.ajedrez.domain.model.PartidaAjedrez;
import com.uade.ajedrez.domain.model.Pieza;
import com.uade.ajedrez.domain.model.ResultadoMovimiento;

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