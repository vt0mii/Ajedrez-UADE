package com.uade.ajedrez.application.port.out;

import com.uade.ajedrez.domain.model.ChessGame;
import com.uade.ajedrez.domain.model.GameId;

import java.util.Optional;

// Puerto de salida para guardar y buscar partidas. Las implementaciones
// (archivos, memoria, lo que venga) viven en infraestructura.
public interface GameRepositoryPort {
    void save(ChessGame game);

    Optional<ChessGame> findById(GameId id);
}
