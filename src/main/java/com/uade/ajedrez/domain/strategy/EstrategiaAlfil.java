package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.Motivo;
import com.uade.ajedrez.domain.model.Movimiento;
import com.uade.ajedrez.domain.model.Pieza;
import com.uade.ajedrez.domain.model.Posicion;
import com.uade.ajedrez.domain.model.ResultadoMovimiento;
import com.uade.ajedrez.domain.model.Tablero;

public class EstrategiaAlfil implements IEstrategiaMovimiento {

    @Override
    public ResultadoMovimiento canMove(Tablero t, Movimiento m, Pieza p) {
        Posicion origen = m.getOrigen();
        Posicion destino = m.getDestino();

        int difFila = Math.abs(destino.getFila() - origen.getFila());
        int difColumna = Math.abs(destino.getColumna() - origen.getColumna());

        if (difFila != difColumna) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "El alfil solo se mueve en diagonal");
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
        int colOrigen = origen.getColumna();
        int colDestino = destino.getColumna();

        // Direccion (Positivo o negativo)
        int direccionFila = (filaDestino > filaOrigen) ? 1 : -1;
        int direccionCol = (colDestino > colOrigen) ? 1 : -1;

        // Casillas intermedias
        int fila = filaOrigen + direccionFila;
        int col = colOrigen + direccionCol;
        while (fila != filaDestino && col != colDestino) {
            if (t.obtenerPieza(new Posicion(fila, col)) != null) {
                return false;
            }
            fila += direccionFila;
            col += direccionCol;
        }

        return true;
    }
}
