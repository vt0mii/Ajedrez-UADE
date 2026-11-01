package com.uade.ajedrez.infrastructure.adapter.out.persistence;

import com.uade.ajedrez.application.port.out.GameRepositoryPort;
import com.uade.ajedrez.application.port.out.SavedGame;
import com.uade.ajedrez.domain.exception.GamePersistenceException;
import com.uade.ajedrez.domain.model.ChessGame;
import com.uade.ajedrez.domain.model.GameId;
import com.uade.ajedrez.domain.service.MovementService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    @Override
    public List<SavedGame> findAll() {
        if (!Files.isDirectory(storageDirectory)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(storageDirectory)) {
            return files
                    .filter(path -> path.getFileName().toString().endsWith(".properties"))
                    .map(this::loadSaved)
                    .flatMap(Optional::stream)
                    .sorted(Comparator.comparingLong(SavedGame::updatedAt).reversed())
                    .toList();
        } catch (IOException e) {
            throw new GamePersistenceException("Could not list games in " + storageDirectory, e);
        }
    }

    public Path getStorageDirectory() {
        return storageDirectory;
    }

    // Un archivo roto no debería romper el listado completo.
    private Optional<SavedGame> loadSaved(Path file) {
        try {
            long updatedAt = Files.getLastModifiedTime(file).toMillis();
            return Optional.of(new SavedGame(GameSerializer.loadFromFile(file, movementService), updatedAt));
        } catch (IOException | RuntimeException e) {
            return Optional.empty();
        }
    }

    private Path fileFor(GameId id) {
        return storageDirectory.resolve(id.getValue() + ".properties");
    }
}
