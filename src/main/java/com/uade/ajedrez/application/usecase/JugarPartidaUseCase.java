package com.uade.ajedrez.application.usecase;

import com.uade.ajedrez.application.port.in.IJugarPartidaPort;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.port.out.IPartidaPersistencia;

public class JugarPartidaUseCase implements IJugarPartidaPort {
    private final PartidaAjedrez partida;
    private final IPartidaPersistencia persistencia;

    public JugarPartidaUseCase(PartidaAjedrez partida, IPartidaPersistencia persistencia) {
        this.partida = partida;
        this.persistencia = persistencia;
    }

    @Override
    public ResultadoMovimiento mover(Movimiento movimiento) {
        return partida.realizarMovimiento(movimiento);
    }

    @Override
    public Estado obtenerEstado() {
        return partida.getEstado();
    }

    @Override
    public Color obtenerTurno() {
        return partida.getTurnoActual();
    }

    @Override
    public Tablero obtenerTablero() {
        return partida.getTablero();
    }

    @Override
    public Jugador obtenerJugadorEnTurno() {
        return partida.getJugadorEnTurno();
    }
}
