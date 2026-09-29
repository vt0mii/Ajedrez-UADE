package com.uade.ajedrez.infrastructure.adapter.in;

import com.uade.ajedrez.domain.port.in.IJugarPartidaPort;

public class ConsolaAdapter {
    private final IJugarPartidaPort jugarPartida;

    public ConsolaAdapter(IJugarPartidaPort jugarPartida) {
        this.jugarPartida = jugarPartida;
    }
}
