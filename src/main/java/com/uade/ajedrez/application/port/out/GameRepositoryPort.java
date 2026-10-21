package com.uade.ajedrez.application.port.out;

import com.uade.ajedrez.domain.model.ChessGame;
import com.uade.ajedrez.domain.model.GameId;

import java.util.Optional;

/**
 * Puerto de salida. Las implementaciones viven en infraestructura (archivos,
 * memoria, base de datos, etc.) y no deben filtrar sus detalles a la aplicación.
 */
public interface GameRepositoryPort {
    void save(ChessGame game);

    Optional<ChessGame> findById(GameId id);
}
