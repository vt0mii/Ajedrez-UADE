package com.uade.ajedrez.infrastructure.adapter.out.persistence;

import com.uade.ajedrez.application.port.out.GameRepositoryPort;
import com.uade.ajedrez.domain.model.ChessGame;
import com.uade.ajedrez.domain.model.GameId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Repositorio en memoria. Va bien para tests, demos o para no persistir nada.
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
