package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.Movimiento;
import com.uade.ajedrez.domain.model.Pieza;
import com.uade.ajedrez.domain.model.ResultadoMovimiento;
import com.uade.ajedrez.domain.model.Tablero;

public interface IEstrategiaMovimiento {
    ResultadoMovimiento canMove(Tablero t, Movimiento m, Pieza p);
}
