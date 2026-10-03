package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.Movimiento;
import com.uade.ajedrez.domain.model.Pieza;
import com.uade.ajedrez.domain.model.ResultadoMovimiento;
import com.uade.ajedrez.domain.model.Tablero;

public class EstrategiaReina implements IEstrategiaMovimiento {

    private final IEstrategiaMovimiento estrategiaTorre = new EstrategiaTorre();
    private final IEstrategiaMovimiento estrategiaAlfil = new EstrategiaAlfil();

    @Override
    public ResultadoMovimiento canMove(Tablero tab, Movimiento mov, Pieza p) {
        Posicion origen = mov.getOrigen();
        Posicion destino = mov.getDestino();

        int difFila = Math.abs(destino.getFila() - origen.getFila());
        int difColumna = Math.abs(destino.getColumna() - origen.getColumna());

        if (difFila == 0 || difColumna == 0) {
            return estrategiaTorre.canMove(tab, mov, p);
        }

        if (difFila == difColumna) {
            return estrategiaAlfil.canMove(tab, mov, p);
        }

        return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "La reina solo se mueve en linea recta o diagonal");
    }
}
