package com.uade.ajedrez.application.usecase;

import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.port.in.IJugarPartidaPort;

public class JugarPartidaUseCase implements IJugarPartidaPort {
    private final PartidaAjedrez partida;

    public JugarPartidaUseCase(PartidaAjedrez partida) {
        this.partida = partida;
    }

    @Override
    public ResultadoMovimiento mover(Movimiento movimiento) {
        return null;
    }

    @Override
    public Estado obtenerEstado() {
        return null;
    }

    @Override
    public Color obtenerTurno() {
        return null;
    }

    @Override
    public Tablero obtenerTablero() {
        return null;
    }
}
