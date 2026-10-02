package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.Color;
import com.uade.ajedrez.domain.model.Motivo;
import com.uade.ajedrez.domain.model.Movimiento;
import com.uade.ajedrez.domain.model.Pieza;
import com.uade.ajedrez.domain.model.ResultadoMovimiento;
import com.uade.ajedrez.domain.model.Tablero;
import com.uade.ajedrez.domain.model.Posicion;

public class EstrategiaPeon implements IEstrategiaMovimiento {

    @Override
    public ResultadoMovimiento canMove(Tablero t, Movimiento m, Pieza p) {
        Posicion origen = m.getOrigen();
        Posicion destino = m.getDestino();

        // Las blancas suben (resta indice), las negras bajan (suman indice)
        int direccion = (p.getColor() == Color.BLANCO) ? -1 : 1;
        int filaInicial = (p.getColor() == Color.BLANCO) ? 6 : 1;

        int difFila = destino.getFila() - origen.getFila();
        int difColumna = Math.abs(destino.getColumna() - origen.getColumna());
        
        // Avance simple
        if (difFila == direccion && difColumna == 0) {
            if (t.obtenerPieza(destino) == null) {
                return new ResultadoMovimiento(true, null, "Movimiento valido");
            } else {
                return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "Camino bloqueado");
            }
        }

        // Avance doble
        if (origen.getFila() == filaInicial && difFila == (2 * direccion) && difColumna == 0) {
            Posicion intermedio = new Posicion(origen.getFila() + direccion, origen.getColumna());
            if (t.obtenerPieza(intermedio) == null && t.obtenerPieza(destino) == null) {
                return new ResultadoMovimiento(true, null, "Movimiento valido");
            } else {
                return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "Camino bloqueado");
            }
        }

        // Captura
        if (difFila == direccion && difColumna == 1) {
            Pieza pDestino = t.obtenerPieza(destino);
            if (pDestino != null && pDestino.getColor() != p.getColor()) {
                return new ResultadoMovimiento(true, Motivo.CAPTURA, "Captura valida");
            }
        }

        return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "Movimiento invalido para el peon");
    }
    
}
