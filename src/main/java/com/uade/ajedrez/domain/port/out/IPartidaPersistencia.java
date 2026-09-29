package com.uade.ajedrez.domain.port.out;

import com.uade.ajedrez.domain.model.PartidaAjedrez;

public interface IPartidaPersistencia {
    void guardar(PartidaAjedrez partida);
    PartidaAjedrez cargar(String id);
}