package com.uade.ajedrez.infrastructure.adapter.out.persistence;

import com.uade.ajedrez.application.port.out.GameRepositoryPort;
import com.uade.ajedrez.application.port.out.SavedGame;
import com.uade.ajedrez.domain.model.ChessGame;
import com.uade.ajedrez.domain.model.GameId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Repositorio en memoria. Va bien para tests, demos o para no persistir nada.
public class InMemoryGameRepository implements GameRepositoryPort {

    private final Map<String, SavedGame> games = new ConcurrentHashMap<>();

    @Override
    public void save(ChessGame game) {
        games.put(game.getId().getValue(), new SavedGame(game, System.currentTimeMillis()));
    }

    @Override
    public Optional<ChessGame> findById(GameId id) {
        SavedGame saved = games.get(id.getValue());
        return saved == null ? Optional.empty() : Optional.of(saved.game());
    }

    @Override
    public List<SavedGame> findAll() {
        return List.copyOf(games.values());
    }
}
