package com.uade.ajedrez.domain.util;

import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.factory.PieceFactory;
import com.uade.ajedrez.domain.exception.GamePersistenceException;
import com.uade.ajedrez.domain.service.MovementService;

import java.io.*;
import java.util.Properties;

public class GameSerializer {
    // Verificar si funciona correctamente. Parece mas simple que JSON por ahora.
    // Esto se reemplazaria a futuro con una BD.
    public static Properties toProperties(ChessManager manager) {
        Properties props = new Properties();

        props.setProperty("state", manager.getState().name());

        Player currentPlayer = manager.getCurrentPlayer();
        if (currentPlayer != null) {
            props.setProperty("playerName", currentPlayer.getName());
            props.setProperty("playerColor", currentPlayer.getColor().name());
        }

        props.setProperty("turnNumber", String.valueOf(manager.getTurn().getTurnNumber()));

        Board board = manager.getBoard();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = board.getPiece(new Position(row, column));
                if (piece != null) {
                    String key = "piece." + row + "," + column;
                    String value = piece.getType().name() + "," + piece.getColor().name();
                    props.setProperty(key, value);
                }
            }
        }

        return props;
    }

    public static ChessManager fromProperties(Properties props) {
        try {
            return deserialize(props);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new GamePersistenceException("Invalid saved game data", e);
        }
    }

    private static ChessManager deserialize(Properties props) {
        String playerName = props.getProperty("playerName", "Jugador");
        Color playerColor = Color.valueOf(props.getProperty("playerColor", "WHITE"));
        Player currentPlayer = new Player(playerName, playerColor);

        Player opponent = new Player("Oponente", 
                playerColor == Color.WHITE ? Color.BLACK : Color.WHITE);

        Board board = new Board();
        // Limpiar el tablero para cargar solo las piezas guardadas
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                board.getSquare(new Position(row, column)).setPiece(null);
            }
        }

        // Cargar piezas
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("piece.")) {
                String[] coords = key.substring(6).split(",");
                if (coords.length != 2) {
                    throw new IllegalArgumentException("Invalid piece coordinates: " + key);
                }
                int row = Integer.parseInt(coords[0]);
                int column = Integer.parseInt(coords[1]);

                String[] parts = props.getProperty(key).split(",");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid piece data: " + key);
                }
                PieceType type = PieceType.valueOf(parts[0]);
                Color color = Color.valueOf(parts[1]);

                board.getSquare(new Position(row, column)).setPiece(PieceFactory.create(type, color));
            }
        }

        ChessManager manager = new ChessManager(
                playerColor == Color.WHITE ? currentPlayer : opponent,
                playerColor == Color.WHITE ? opponent : currentPlayer,
                board,
                new MovementService()
        );

        State state = State.valueOf(props.getProperty("state", "IN_PROGRESS"));
        manager.setState(state);

        return manager;
    }

    public static void saveToFile(ChessManager manager, String filename) {
        Properties props = toProperties(manager);
        try (FileOutputStream out = new FileOutputStream(filename)) {
            props.store(out, "Partida de Ajedrez");
        } catch (IOException e) {
            throw new GamePersistenceException("Error al guardar la partida", e);
        }
    }

    public static ChessManager loadFromFile(String filename) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(filename)) {
            props.load(in);
        } catch (IOException | IllegalArgumentException e) {
            throw new GamePersistenceException("Error al cargar la partida", e);
        }
        return fromProperties(props);
    }
}
