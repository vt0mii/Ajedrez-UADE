package com.uade.ajedrez.infrastructure.adapter.in;

import com.uade.ajedrez.infrastructure.port.in.IJugarPartidaPort;

public class ConsolaAdapter {
    @SuppressWarnings("unused")
    private final IJugarPartidaPort jugarPartida;

    public ConsolaAdapter(IJugarPartidaPort jugarPartida) {
        this.jugarPartida = jugarPartida;
    }
}
