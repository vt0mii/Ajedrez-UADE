package main.java.com.uade.ajedrez.model.strategy;

import main.java.com.uade.ajedrez.model.Movimiento;
import main.java.com.uade.ajedrez.model.ResultadoMovimiento;
import main.java.com.uade.ajedrez.model.Tablero;

public interface IEstrategiaMovimiento {
    ResultadoMovimiento canMove(Tablero t, Movimiento m);
}
