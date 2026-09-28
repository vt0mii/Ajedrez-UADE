package com.uade.ajedrez.model.strategy;

import com.uade.ajedrez.model.Movimiento;
import com.uade.ajedrez.model.ResultadoMovimiento;
import com.uade.ajedrez.model.Tablero;

public interface IEstrategiaMovimiento {
    ResultadoMovimiento canMove(Tablero t, Movimiento m);
}
