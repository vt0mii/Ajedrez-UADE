package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.domain.model.*;

public interface IJugarPartidaPort {
    ResultadoMovimiento mover(Movimiento movimiento);
    Estado obtenerEstado();
    Color obtenerTurno();
    Tablero obtenerTablero();
    Jugador obtenerJugadorEnTurno();
}
