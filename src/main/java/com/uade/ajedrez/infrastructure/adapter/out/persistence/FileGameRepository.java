package com.uade.ajedrez.infrastructure.adapter.out.persistence;

import com.uade.ajedrez.application.port.out.GameRepositoryPort;
import com.uade.ajedrez.domain.model.ChessGame;
import com.uade.ajedrez.domain.model.GameId;
import com.uade.ajedrez.domain.service.MovementService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

// Guarda y carga partidas como archivos. El id se valida en GameId, así que
// no hay forma de salir del directorio de partidas.
public class FileGameRepository implements GameRepositoryPort {

    private final Path storageDirectory;
    private final MovementService movementService;

    public FileGameRepository(Path storageDirectory, MovementService movementService) {
        this.storageDirectory = storageDirectory;
        this.movementService = movementService;
    }

    @Override
    public void save(ChessGame game) {
        GameSerializer.saveToFile(game, fileFor(game.getId()));
    }

    @Override
    public Optional<ChessGame> findById(GameId id) {
        Path file = fileFor(id);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        return Optional.of(GameSerializer.loadFromFile(file, movementService));
    }

    public Path getStorageDirectory() {
        return storageDirectory;
    }

    private Path fileFor(GameId id) {
        return storageDirectory.resolve(id.getValue() + ".properties");
    }
}
