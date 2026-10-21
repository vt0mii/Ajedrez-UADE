package com.uade.ajedrez.infrastructure.adapter.out.persistence;

import com.uade.ajedrez.application.port.out.GameRepositoryPort;
import com.uade.ajedrez.domain.model.ChessGame;
import com.uade.ajedrez.domain.model.GameId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador en memoria. Útil para tests, demos o cuando no se quiere persistir.
 */
public class InMemoryGameRepository implements GameRepositoryPort {

    private final Map<String, ChessGame> games = new ConcurrentHashMap<>();

    @Override
    public void save(ChessGame game) {
        games.put(game.getId().getValue(), game);
    }

    @Override
    public Optional<ChessGame> findById(GameId id) {
        return Optional.ofNullable(games.get(id.getValue()));
    }
}
