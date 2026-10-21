package com.uade.ajedrez.application.service;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.GameView.MoveView;
import com.uade.ajedrez.application.dto.GameView.PieceView;
import com.uade.ajedrez.application.dto.GameView.PlayerView;
import com.uade.ajedrez.domain.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Convierte el agregado de dominio en el modelo de lectura que consumen los
 * adaptadores de entrada.
 */
public final class GameViewMapper {

    private GameViewMapper() {
    }

    public static GameView toView(ChessGame game) {
        Board board = game.getBoard();
        PieceView[][] pieces = new PieceView[Board.SIZE][Board.SIZE];
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = board.getPiece(new Position(row, column));
                if (piece != null) {
                    pieces[row][column] = new PieceView(piece.getType(), piece.getColor());
                }
            }
        }

        List<MoveView> history = new ArrayList<>();
        for (Move move : game.getHistory()) {
            history.add(toMoveView(move));
        }

        Turn turn = game.getTurn();
        Player white = turn.getPlayer(Color.WHITE);
        Player black = turn.getPlayer(Color.BLACK);

        return new GameView(
                game.getId().getValue(),
                game.getState(),
                game.getCurrentTurn(),
                turn.getTurnNumber(),
                new PlayerView(white.getName(), white.getColor()),
                new PlayerView(black.getName(), black.getColor()),
                pieces,
                List.copyOf(history),
                history.isEmpty() ? null : history.get(history.size() - 1),
                winnerOf(game));
    }

    private static MoveView toMoveView(Move move) {
        return new MoveView(move.getOrigin().toAlgebraic(), move.getDestination().toAlgebraic());
    }

    private static Color winnerOf(ChessGame game) {
        if (game.getState() != GameState.CHECKMATE) {
            return null;
        }
        // El jugador que tiene el turno es el que recibió el jaque mate.
        return game.getCurrentTurn() == Color.WHITE ? Color.BLACK : Color.WHITE;
    }
}