package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.Motivo;
import com.uade.ajedrez.domain.model.Movimiento;
import com.uade.ajedrez.domain.model.Pieza;
import com.uade.ajedrez.domain.model.Posicion;
import com.uade.ajedrez.domain.model.ResultadoMovimiento;
import com.uade.ajedrez.domain.model.Tablero;

public class EstrategiaCaballo implements IEstrategiaMovimiento {

    @Override
    public ResultadoMovimiento canMove(Tablero tab, Movimiento mov, Pieza p) {
        Posicion origen = mov.getOrigen();
        Posicion destino = mov.getDestino();

        int difFila = Math.abs(destino.getFila() - origen.getFila());
        int difColumna = Math.abs(destino.getColumna() - origen.getColumna());

        if (!((difFila == 2 && difColumna == 1) || (difFila == 1 && difColumna == 2))) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "El caballo solo se mueve en forma de L");
        }

        Pieza pDestino = tab.obtenerPieza(destino);
        if (pDestino == null) {
            return new ResultadoMovimiento(true, null, "Movimiento valido");
        } else if (pDestino.getColor() != p.getColor()) {
            return new ResultadoMovimiento(true, Motivo.CAPTURA, "Captura valida");
        } else {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "No es posible capturar una pieza propia");
        }
    }
    
}
