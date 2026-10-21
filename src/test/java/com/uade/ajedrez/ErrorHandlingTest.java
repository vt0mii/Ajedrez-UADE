package com.uade.ajedrez;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.port.in.ChessGameUseCases;
import com.uade.ajedrez.application.service.ChessGameService;
import com.uade.ajedrez.domain.exception.GamePersistenceException;
import com.uade.ajedrez.domain.model.Color;
import com.uade.ajedrez.domain.model.GameState;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.infrastructure.adapter.in.console.ConsoleRenderer;
import com.uade.ajedrez.infrastructure.adapter.in.console.ConsoleView;
import com.uade.ajedrez.infrastructure.adapter.out.persistence.GameSerializer;
import com.uade.ajedrez.infrastructure.adapter.out.persistence.InMemoryGameRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class ErrorHandlingTest {

    public static void main(String[] args) throws Exception {
        testConsoleHandlesInvalidInput();
        testConsoleHandlesEof();
        testPersistenceErrors();
        System.out.println("Error handling tests passed!");
    }

    static void testConsoleHandlesInvalidInput() {
        RecordingLogger logger = new RecordingLogger();
        ChessGameUseCases useCases = newService(logger);
        String input = "\n\n" + "bad\n" + "z9 e4\n" + "e2 e5\n" + "e2 e4\n" + "quit\n";
        ConsoleOutput console = runConsole(useCases, logger, input);

        assert logger.warnings.size() == 3
                : "Se esperaban 3 warnings (formato, coordenada y movimiento rechazado), hubo "
                        + logger.warnings.size();
        assert logger.infos.stream().anyMatch(line -> line.contains("Move completed"))
                : "El movimiento valido deberia registrarse";
        assert console.text.contains("Invalid format") : "Falta el error de formato";
        assert console.text.contains("Error: Coordinate out of board")
                : "Falta el error de coordenada";
        assert console.text.contains("Invalid move:") : "Falta el rechazo del movimiento";
        assert console.text.contains("Game abandoned.") : "Quit deberia abandonar la partida";

        String gameId = logger.infos.stream()
                .filter(line -> line.startsWith("Game created: "))
                .map(line -> line.substring("Game created: ".length()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No se registro la creacion de la partida"));

        GameView game = useCases.getGame(gameId);
        assert game.status() == GameState.ABANDONED : "La partida deberia quedar abandonada";
        assert game.turn() == Color.BLACK : "Solo el movimiento valido deberia cambiar el turno";
        assert game.history().size() == 1 : "Solo el movimiento valido deberia registrarse";

        System.out.println("V testConsoleHandlesInvalidInput paso");
    }

    static void testConsoleHandlesEof() {
        RecordingLogger logger = new RecordingLogger();
        ChessGameUseCases useCases = newService(logger);
        ConsoleOutput console = runConsole(useCases, logger, "\n\n");

        assert console.text.contains("Input closed. Game stopped.") : "El EOF deberia terminar normalmente";
        assert logger.infos.stream().anyMatch(line -> line.contains("Game stopped: end of input"))
                : "El EOF deberia registrarse";

        System.out.println("V testConsoleHandlesEof paso");
    }

    static void testPersistenceErrors() throws IOException {
        Properties corrupt = new Properties();
        corrupt.setProperty("formatVersion", "3");
        corrupt.setProperty("piece.bad", "PAWN,WHITE");
        try {
            GameSerializer.fromProperties(corrupt, new MovementService());
            throw new AssertionError("Los datos corruptos deberian fallar");
        } catch (GamePersistenceException e) {
            assert e.getCause() instanceof IllegalArgumentException : "Deberia conservar la causa";
        }

        Path directory = Files.createTempDirectory("chess-errors-");
        try {
            try {
                GameSerializer.loadFromFile(directory.resolve("missing.properties"), new MovementService());
                throw new AssertionError("Un archivo inexistente deberia fallar");
            } catch (GamePersistenceException e) {
                assert e.getCause() instanceof IOException : "Deberia conservar la causa";
            }
        } finally {
            Files.deleteIfExists(directory);
        }

        System.out.println("V testPersistenceErrors paso");
    }

    private static ChessGameUseCases newService(GameLogger logger) {
        return new ChessGameService(new InMemoryGameRepository(), new MovementService(), logger);
    }

    private static ConsoleOutput runConsole(ChessGameUseCases useCases, GameLogger logger, String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConsoleRenderer renderer = new ConsoleRenderer(new PrintStream(output, true, StandardCharsets.UTF_8));
        ConsoleView view = new ConsoleView(useCases, renderer, new Scanner(new StringReader(input)), logger);
        view.start();
        return new ConsoleOutput(output.toString(StandardCharsets.UTF_8));
    }

    private record ConsoleOutput(String text) {
    }

    private static class RecordingLogger implements GameLogger {
        final List<String> infos = new ArrayList<>();
        final List<String> warnings = new ArrayList<>();

        public void info(String message) {
            infos.add(message);
        }

        public void warning(String message) {
            warnings.add(message);
        }

        public void error(String message, Throwable cause) {
            throw new AssertionError(message, cause);
        }
    }
}
