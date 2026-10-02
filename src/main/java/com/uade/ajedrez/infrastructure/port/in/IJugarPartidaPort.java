package com.uade.ajedrez.infrastructure.port.in;

import com.uade.ajedrez.domain.model.*;

public interface IJugarPartidaPort {
    ResultadoMovimiento mover(Movimiento movimiento);
    Estado obtenerEstado();
    Color obtenerTurno();
    Tablero obtenerTablero();
}