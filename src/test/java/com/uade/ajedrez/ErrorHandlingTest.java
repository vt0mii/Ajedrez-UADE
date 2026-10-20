package com.uade.ajedrez;

import com.uade.ajedrez.domain.exception.GamePersistenceException;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.domain.util.GameSerializer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ErrorHandlingTest {
    public static void main(String[] args) throws Exception {
        RecordingLogger logger = new RecordingLogger();
        ChessManager manager = new ChessManager(new Player("White", Color.WHITE),
                new Player("Black", Color.BLACK), new Board(), new MovementService(), logger);
        List<String> output = new ArrayList<>();
        Iterator<String> input = Arrays.asList("bad", "z9 e4", "e2 e5", "e2   e4", "quit").iterator();
        manager.playGame(() -> input.hasNext() ? input.next() : null, output::add);
        assert manager.getCurrentTurn() == Color.BLACK : "Only the valid move should change turns";
        assert logger.warnings.size() == 3 : "Malformed input and rejected moves should be logged";
        assert output.stream().filter(line -> line.startsWith("Error:")).count() == 2;
        assert logger.infos.stream().anyMatch(line -> line.contains("Move completed"));
        manager.playGame(() -> null, output::add);
        assert output.contains("Input closed. Game stopped.") : "EOF must terminate normally";

        Properties corrupt = new Properties();
        corrupt.setProperty("piece.bad", "PAWN,WHITE");
        try {
            GameSerializer.fromProperties(corrupt);
            throw new AssertionError("Corrupt data should fail");
        } catch (GamePersistenceException e) {
            assert e.getCause() instanceof IllegalArgumentException;
        }
        Path directory = Files.createTempDirectory("chess-errors-");
        try {
            try {
                GameSerializer.loadFromFile(directory.resolve("missing.properties").toString());
                throw new AssertionError("Missing file should fail");
            } catch (GamePersistenceException e) {
                assert e.getCause() instanceof java.io.IOException;
            }
            try {
                GameSerializer.saveToFile(manager, directory.toString());
                throw new AssertionError("Saving to a directory should fail");
            } catch (GamePersistenceException e) {
                assert e.getCause() instanceof java.io.IOException;
            }
        } finally {
            Files.delete(directory);
        }
        System.out.println("Error handling tests passed!");
    }

    private static class RecordingLogger implements GameLogger {
        final List<String> infos = new ArrayList<>();
        final List<String> warnings = new ArrayList<>();
        public void info(String message) { infos.add(message); }
        public void warning(String message) { warnings.add(message); }
        public void error(String message, Throwable cause) { throw new AssertionError(message, cause); }
    }
}
