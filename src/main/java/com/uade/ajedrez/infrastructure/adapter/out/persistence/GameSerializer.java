package com.uade.ajedrez.infrastructure.adapter.out.persistence;

import com.uade.ajedrez.domain.exception.GamePersistenceException;
import com.uade.ajedrez.domain.factory.PieceFactory;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.MovementService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Serializador basado en Properties. Es un detalle de infraestructura: el
 * dominio no sabe que las partidas se guardan como archivos de texto.
 */
public final class GameSerializer {

    private static final String FORMAT_VERSION = "3";

    private GameSerializer() {
    }

    public static void saveToFile(ChessGame game, Path file) {
        Properties properties = toProperties(game);
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            try (OutputStream out = Files.newOutputStream(file)) {
                properties.store(out, "Chess game");
            }
        } catch (IOException e) {
            throw new GamePersistenceException("Could not save game to " + file, e);
        }
    }

    public static ChessGame loadFromFile(Path file, MovementService movementService) {
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            properties.load(in);
        } catch (IOException e) {
            throw new GamePersistenceException("Could not load game from " + file, e);
        }
        return fromProperties(properties, movementService);
    }

    public static Properties toProperties(ChessGame game) {
        Properties properties = new Properties();
        properties.setProperty("formatVersion", FORMAT_VERSION);
        properties.setProperty("id", game.getId().getValue());
        properties.setProperty("state", game.getState().name());
        properties.setProperty("turnIndex", String.valueOf(game.getTurn().getCurrentIndex()));
        properties.setProperty("turnNumber", String.valueOf(game.getTurn().getTurnNumber()));
        properties.setProperty("whitePlayer", game.getTurn().getPlayer(Color.WHITE).getName());
        properties.setProperty("blackPlayer", game.getTurn().getPlayer(Color.BLACK).getName());

        Board board = game.getBoard();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = board.getPiece(new Position(row, column));
                if (piece != null) {
                    properties.setProperty(
                            "piece." + row + "," + column,
                            piece.getType().name() + "," + piece.getColor().name());
                }
            }
        }

        List<Move> history = game.getHistory();
        properties.setProperty("historySize", String.valueOf(history.size()));
        for (int i = 0; i < history.size(); i++) {
            Move move = history.get(i);
            properties.setProperty("history." + i,
                    move.getOrigin().toAlgebraic() + "," + move.getDestination().toAlgebraic());
        }

        return properties;
    }

    public static ChessGame fromProperties(Properties properties, MovementService movementService) {
        try {
            return deserialize(properties, movementService);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new GamePersistenceException("Invalid saved game data", e);
        }
    }

    private static ChessGame deserialize(Properties properties, MovementService movementService) {
        String version = properties.getProperty("formatVersion");
        if (!FORMAT_VERSION.equals(version)) {
            throw new IllegalArgumentException("Unsupported game file format: " + version);
        }
        if (movementService == null) {
            throw new IllegalArgumentException("A movement service is required to load a game");
        }

        GameId id = new GameId(properties.getProperty("id"));
        GameState state = GameState.valueOf(properties.getProperty("state", GameState.IN_PROGRESS.name()));
        int turnIndex = Integer.parseInt(properties.getProperty("turnIndex", "0"));
        int turnNumber = Integer.parseInt(properties.getProperty("turnNumber", "1"));

        Player whitePlayer = new Player(properties.getProperty("whitePlayer", "White"), Color.WHITE);
        Player blackPlayer = new Player(properties.getProperty("blackPlayer", "Black"), Color.BLACK);

        Board board = Board.empty();
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith("piece.")) {
                continue;
            }
            String[] coordinates = key.substring("piece.".length()).split(",");
            if (coordinates.length != 2) {
                throw new IllegalArgumentException("Invalid piece coordinates: " + key);
            }
            int row = Integer.parseInt(coordinates[0]);
            int column = Integer.parseInt(coordinates[1]);

            String[] parts = properties.getProperty(key).split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid piece data: " + key);
            }
            PieceType type = PieceType.valueOf(parts[0]);
            Color color = Color.valueOf(parts[1]);
            board.getSquare(new Position(row, column)).setPiece(PieceFactory.create(type, color));
        }

        int historySize = Integer.parseInt(properties.getProperty("historySize", "0"));
        List<Move> history = new ArrayList<>();
        for (int i = 0; i < historySize; i++) {
            String value = properties.getProperty("history." + i);
            if (value == null) {
                continue;
            }
            String[] squares = value.split(",");
            history.add(new Move(Position.fromAlgebraic(squares[0]), Position.fromAlgebraic(squares[1])));
        }

        Turn turn = Turn.restore(whitePlayer, blackPlayer, turnIndex, turnNumber);
        return ChessGame.restore(id, board, movementService, turn, state, history);
    }
}
