package com.uade.ajedrez.infrastructure.adapter.out;

import com.uade.ajedrez.domain.model.PartidaAjedrez;
import com.uade.ajedrez.domain.port.out.IPartidaPersistencia;

import java.util.HashMap;
import java.util.Map;

public class MemoriaPartida implements IPartidaPersistencia {
    private final Map<String, PartidaAjedrez> partidas = new HashMap<>();

    @Override
    public void guardar(PartidaAjedrez partida) {
        // TODO: Implementar guardado en memoria
    }

    @Override
    public PartidaAjedrez cargar(String id) {
        // TODO: Implementar carga desde memoria
        return partidas.get(id);
    }
}
