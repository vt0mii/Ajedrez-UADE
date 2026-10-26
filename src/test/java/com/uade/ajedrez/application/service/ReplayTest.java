package com.uade.ajedrez.application.service;

import com.uade.ajedrez.application.dto.*;
import com.uade.ajedrez.application.exception.InvalidCommandException;
import com.uade.ajedrez.application.exception.GameNotFoundException;
import com.uade.ajedrez.application.port.out.GameRepositoryPort;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.infrastructure.adapter.out.persistence.*;
import com.uade.ajedrez.infrastructure.adapter.in.console.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class ReplayTest {
    public static void main(String[] args) throws Exception {
        testCaptureAndIsolation();
        testActiveGamesRejected();
        testCheckmate();
        testEmptyHistory();
        testSavedGameUnchanged();
        testConsole();
        System.out.println("Replay tests passed!");
    }

    private static void testCaptureAndIsolation() {
        CountingRepository repository = new CountingRepository(new InMemoryGameRepository());
        ChessGameService service = new ChessGameService(repository, new MovementService());
        String id = create(service);
        move(service, id, "e2", "e4");
        move(service, id, "d7", "d5");
        move(service, id, "e4", "d5");
        GameView original = service.abandonGame(id);
        int saves = repository.saves;
        ReplaySession replay = service.startReplay(id);
        assert replay.current().position() == 0;
        assert replay.previous().position() == 0;
        assert replay.current().game().board()[6][4].type() == PieceType.PAWN;
        assert replay.next().game().turn() == Color.BLACK;
        assert replay.next().game().turnNumber() == 2;
        GameView capture = replay.next().game();
        assert capture.board()[3][3].color() == Color.WHITE;
        assert capture.board()[4][4] == null;
        assert replay.next().position() == 3;
        GameView beforeCapture = replay.previous().game();
        assert beforeCapture.board()[3][3].color() == Color.BLACK;
        assert beforeCapture.board()[4][4].color() == Color.WHITE;
        assert replay.next().game().board()[3][3].color() == Color.WHITE;
        while (replay.current().position() > 0) { replay.previous(); }
        assert replay.current().game().history().isEmpty();
        assert replay.current().game().board()[1][3].color() == Color.BLACK;
        // Alterar un DTO tampoco puede cambiar la sesion ni la partida real.
        replay.current().game().board()[6][4] = null;
        assert replay.current().game().board()[6][4] != null;
        assert service.startReplay(id).current().position() == 0;
        assert repository.saves == saves : "El replay no debe guardar";
        assertSameGame(original, service.getGame(id));
    }

    private static void testActiveGamesRejected() {
        ChessGameService service = new ChessGameService(new InMemoryGameRepository(), new MovementService());
        String id = create(service);
        rejectReplay(service, id);
        move(service, id, "e2", "e4");
        move(service, id, "f7", "f6");
        move(service, id, "d1", "h5");
        assert service.getGame(id).status() == GameState.CHECK;
        rejectReplay(service, id);
        try {
            service.startReplay("missing");
            throw new AssertionError("Debe rechazar un id inexistente");
        } catch (GameNotFoundException expected) { }
        try {
            service.startReplay("../invalid");
            throw new AssertionError("Debe rechazar un id invalido");
        } catch (InvalidCommandException expected) { }
        ChessGame active = ChessGame.newGame(new GameId("active"), new Player("W", Color.WHITE),
                new Player("B", Color.BLACK), new Board(), new MovementService());
        try {
            active.startReplay();
            throw new AssertionError("El dominio tambien debe rechazar partidas activas");
        } catch (IllegalStateException expected) { }
    }

    private static void testCheckmate() {
        ChessGameService service = new ChessGameService(new InMemoryGameRepository(), new MovementService());
        String id = create(service);
        move(service, id, "f2", "f3");
        move(service, id, "e7", "e5");
        move(service, id, "g2", "g4");
        move(service, id, "d8", "h4");
        GameView original = service.getGame(id);
        assert original.status() == GameState.CHECKMATE;
        ReplaySession replay = service.startReplay(id);
        for (int i = 0; i < 4; i++) { replay.next(); }
        assertSameGame(original, replay.current().game());
        assert replay.previous().game().status() == GameState.IN_PROGRESS;
        assert replay.next().game().status() == GameState.CHECKMATE;
        assertSameGame(original, service.getGame(id));
    }

    private static void testEmptyHistory() {
        ChessGameService service = new ChessGameService(new InMemoryGameRepository(), new MovementService());
        String id = create(service);
        service.abandonGame(id);
        ReplaySession replay = service.startReplay(id);
        assert replay.current().result() == GameState.ABANDONED;
        assert replay.current().totalMoves() == 0;
        assert replay.next().position() == 0;
        assert replay.previous().position() == 0;
    }

    private static void testSavedGameUnchanged() throws Exception {
        Path directory = Files.createTempDirectory("chess-replay-");
        Path file = null;
        try {
            MovementService movement = new MovementService();
            ChessGameService writer = new ChessGameService(new FileGameRepository(directory, movement), movement);
            String id = create(writer);
            move(writer, id, "e2", "e4");
            move(writer, id, "d7", "d5");
            move(writer, id, "e4", "d5");
            writer.abandonGame(id);
            file = directory.resolve(id + ".properties");
            byte[] originalBytes = Files.readAllBytes(file);
            var originalTime = Files.getLastModifiedTime(file);
            CountingRepository repository = new CountingRepository(new FileGameRepository(directory, movement));
            ChessGameService reader = new ChessGameService(repository, movement);
            GameView original = reader.getGame(id);
            ReplaySession replay = reader.startReplay(id);
            for (int i = 0; i < 5; i++) { replay.next(); }
            for (int i = 0; i < 5; i++) { replay.previous(); }
            replay.next();
            assert repository.saves == 0 : "No se debe llamar a save";
            assert Arrays.equals(originalBytes, Files.readAllBytes(file)) : "El archivo debe ser identico";
            assert originalTime.equals(Files.getLastModifiedTime(file)) : "No se debe reescribir el archivo";
            assertSameGame(original, reader.getGame(id));
        } finally {
            if (file != null) { Files.deleteIfExists(file); }
            Files.deleteIfExists(directory);
        }
    }

    private static void testConsole() {
        ChessGameService service = new ChessGameService(new InMemoryGameRepository(), new MovementService());
        String id = create(service);
        service.abandonGame(id);
        String saved = runConsole(service, "replay " + id + "\nprev\nnext\nexit\n");
        assert saved.contains("Result: ABANDONED");
        assert saved.contains("Start of replay.");
        assert saved.contains("End of replay.");
        String fresh = runConsole(service, "\n\ne2 e4\nquit\nreplay\nnext\nprev\nexit\nexit\n");
        assert fresh.contains("move 0/1");
        assert fresh.contains("move 1/1");
    }

    private static String runConsole(ChessGameService service, String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new ConsoleView(service, new ConsoleRenderer(new PrintStream(output, true, StandardCharsets.UTF_8)),
                new Scanner(input)).start();
        return output.toString(StandardCharsets.UTF_8);
    }

    private static void rejectReplay(ChessGameService service, String id) {
        try {
            service.startReplay(id);
            throw new AssertionError("Debe rechazar el replay de una partida activa");
        } catch (InvalidCommandException expected) { }
    }

    private static String create(ChessGameService service) {
        return service.createGame(new NewGameCommand("White", "Black")).id();
    }

    private static void move(ChessGameService service, String id, String from, String to) {
        service.makeMove(id, new MoveCommand(from, to));
    }

    private static void assertSameGame(GameView expected, GameView actual) {
        assert expected.id().equals(actual.id());
        assert expected.status() == actual.status();
        assert expected.turn() == actual.turn();
        assert expected.turnNumber() == actual.turnNumber();
        assert expected.whitePlayer().equals(actual.whitePlayer());
        assert expected.blackPlayer().equals(actual.blackPlayer());
        assert expected.history().equals(actual.history());
        assert expected.winner() == actual.winner();
        assert Arrays.deepEquals(expected.board(), actual.board());
    }

    private static final class CountingRepository implements GameRepositoryPort {
        private final GameRepositoryPort delegate;
        private int saves;
        CountingRepository(GameRepositoryPort delegate) { this.delegate = delegate; }
        public void save(ChessGame game) { saves++; delegate.save(game); }
        public Optional<ChessGame> findById(GameId id) { return delegate.findById(id); }
    }
}
