package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.Color;
import com.uade.ajedrez.domain.model.Motivo;
import com.uade.ajedrez.domain.model.Movimiento;
import com.uade.ajedrez.domain.model.Pieza;
import com.uade.ajedrez.domain.model.Posicion;
import com.uade.ajedrez.domain.model.ResultadoMovimiento;
import com.uade.ajedrez.domain.model.Tablero;

public class EstrategiaTorre implements IEstrategiaMovimiento{

    @Override
    public ResultadoMovimiento canMove(Tablero t, Movimiento m, Pieza p) {
        Posicion origen = m.getOrigen();
        Posicion destino = m.getDestino();

        int difFila = destino.getFila() - origen.getFila();
        int difColumna = destino.getColumna() - origen.getColumna();

        if (difColumna != 0 && difFila != 0) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "La torre solo se mueve en linea recta");
        }

        if (!caminoLibre(origen, destino, t)) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "Camino bloqueado");
        }

        // Ya verificado que el movimiento es posible, determino el motivo de movimiento (Si es captura o movimiento comun)
        Pieza pDestino = t.obtenerPieza(destino);
        if (pDestino == null) {
            return new ResultadoMovimiento(true, null, "Movimiento valido");
        } else if (pDestino.getColor() != p.getColor()) {
            return new ResultadoMovimiento(true, Motivo.CAPTURA, "Captura valida");
        } else {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "No es posible capturar una pieza propia");
        }


    }

    private boolean caminoLibre(Posicion origen, Posicion destino, Tablero t) {
        int filaOrigen = origen.getFila();
        int filaDestino = destino.getFila();
        int columnaOrigen = origen.getColumna();
        int columnaDestino = destino.getColumna();

        // Movimiento vertical
        if (columnaOrigen == columnaDestino) {
            int direccion = (filaDestino > filaOrigen) ? 1 : -1;
            for (int fila = filaOrigen + direccion; fila != filaDestino; fila += direccion) {
                if (t.obtenerPieza(new Posicion(fila, columnaOrigen)) != null) {
                    return false;
                }
            }
        } else { // Movimiento horizontal
            int direccion = (columnaDestino > columnaOrigen) ? 1 : -1;
            for (int columna = columnaOrigen + direccion; columna != columnaOrigen; columna += direccion) {
                if (t.obtenerPieza(new Posicion(filaOrigen, columna)) != null) {
                    return false;
                }
            }
        }

        return true;

    }
    
}
