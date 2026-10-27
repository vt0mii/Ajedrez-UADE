package com.uade.ajedrez.infrastructure.adapter.out.persistence;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.MoveCommand;
import com.uade.ajedrez.application.dto.NewGameCommand;
import com.uade.ajedrez.application.service.ChessGameService;
import com.uade.ajedrez.domain.exception.GamePersistenceException;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.MovementService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Properties;
import java.util.stream.Stream;

public class GameSerializerTest {

    public static void main(String[] args) throws IOException {
        System.out.println("Ejecutando tests de GameSerializer...\n");

        testRoundTripYPersistencia();
        testDatosInvalidos();
        testArchivoInexistente();
        testVersionNoSoportada();
        testGuardarEnDirectorio();

        System.out.println("\nTodos los tests de GameSerializer pasaron!");
    }

    static void testRoundTripYPersistencia() throws IOException {
        Path directory = Files.createTempDirectory("chess-persistence-");
        try {
            MovementService movementService = new MovementService();

            FileGameRepository firstRepository = new FileGameRepository(directory, movementService);
            ChessGameService firstService = new ChessGameService(firstRepository, movementService);
            GameView created = firstService.createGame(new NewGameCommand("Tomi", "Juan"));
            firstService.makeMove(created.id(), new MoveCommand("e2", "e4"));

            // Otra instancia del servicio lee la partida del disco.
            FileGameRepository secondRepository = new FileGameRepository(directory, movementService);
            ChessGameService secondService = new ChessGameService(secondRepository, movementService);
            GameView loaded = secondService.getGame(created.id());

            assert loaded.id().equals(created.id()) : "El id deberia conservarse";
            assert loaded.turn() == Color.BLACK : "Deberia seguir el turno de negras";
            assert loaded.turnNumber() == 1 : "El numero de turno deberia conservarse";
            assert loaded.status() == GameState.IN_PROGRESS : "El estado deberia conservarse";
            assert loaded.whitePlayer().name().equals("Tomi") : "El nombre de blancas deberia conservarse";
            assert loaded.history().size() == 1 : "El historial deberia conservarse";
            assert loaded.board()[4][4] != null : "El peon deberia estar en E4";
            assert loaded.board()[6][4] == null : "E2 deberia estar vacio";

            // Y tiene que poder seguir jugándose (regresión:
            // movementService era null).
            GameView continued = secondService.makeMove(created.id(), new MoveCommand("e7", "e5"));
            assert continued.turn() == Color.WHITE : "El turno deberia volver a blancas";
            assert continued.history().size() == 2 : "Deberia acumular el historial";

            System.out.println("V testRoundTripYPersistencia paso");
        } finally {
            deleteRecursively(directory);
        }
    }

    static void testDatosInvalidos() {
        Properties corrupt = new Properties();
        corrupt.setProperty("formatVersion", "3");
        corrupt.setProperty("piece.bad", "PAWN,WHITE");

        try {
            GameSerializer.fromProperties(corrupt, new MovementService());
            throw new AssertionError("Los datos corruptos deberian fallar");
        } catch (GamePersistenceException e) {
            assert e.getCause() instanceof IllegalArgumentException : "Deberia conservar la causa";
        }

        System.out.println("V testDatosInvalidos paso");
    }

    static void testArchivoInexistente() throws IOException {
        Path directory = Files.createTempDirectory("chess-missing-");
        try {
            try {
                GameSerializer.loadFromFile(directory.resolve("missing.properties"), new MovementService());
                throw new AssertionError("Un archivo inexistente deberia fallar");
            } catch (GamePersistenceException e) {
                assert e.getCause() instanceof IOException : "Deberia conservar la causa";
            }
            System.out.println("V testArchivoInexistente paso");
        } finally {
            deleteRecursively(directory);
        }
    }

    static void testVersionNoSoportada() {
        Properties properties = new Properties();
        properties.setProperty("formatVersion", "1");

        try {
            GameSerializer.fromProperties(properties, new MovementService());
            throw new AssertionError("La version 1 deberia rechazarse");
        } catch (GamePersistenceException e) {
            assert e.getMessage().contains("Invalid saved game data") : "Mensaje inesperado";
        }

        System.out.println("V testVersionNoSoportada paso");
    }

    static void testGuardarEnDirectorio() throws IOException {
        Path directory = Files.createTempDirectory("chess-save-dir-");
        try {
            ChessGame game = ChessGame.newGame(
                    new GameId("g_save_test"),
                    new Player("Blanco", Color.WHITE),
                    new Player("Negro", Color.BLACK),
                    new Board(),
                    new MovementService());

            try {
                GameSerializer.saveToFile(game, directory);
                throw new AssertionError("Guardar sobre un directorio deberia fallar");
            } catch (GamePersistenceException e) {
                assert e.getCause() instanceof IOException : "Deberia conservar la causa";
            }
            System.out.println("V testGuardarEnDirectorio paso");
        } finally {
            deleteRecursively(directory);
        }
    }

    private static void deleteRecursively(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
