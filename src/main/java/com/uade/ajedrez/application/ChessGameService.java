package com.uade.ajedrez.application;

import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.port.in.ChessGame;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.domain.service.MovementService;
import java.util.ArrayList;
import java.util.Objects;

/** Coordinates one in-memory game. Requests and snapshots are atomic. */
public class ChessGameService implements ChessGame {
    private final GameLogger logger;
    private ChessManager manager;

    public ChessGameService(GameLogger logger) {
        this.logger = Objects.requireNonNull(logger);
        newGame();
    }

    @Override
    public synchronized GameSnapshot newGame() {
        manager = new ChessManager(new Player("Blancas", Color.WHITE),
                new Player("Negras", Color.BLACK), new Board(), new MovementService(), logger);
        logger.info("New web game started");
        return getGame();
    }

    @Override
    public synchronized MoveOutcome move(Move move) {
        Objects.requireNonNull(move, "Move is required");
        MoveResult result = manager.makeMove(move);
        return new MoveOutcome(result, getGame());
    }

    @Override
    public synchronized GameSnapshot getGame() {
        var pieces = new ArrayList<GameSnapshot.SquareView>();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = manager.getBoard().getPiece(new Position(row, column));
                if (piece != null) {
                    pieces.add(new GameSnapshot.SquareView(row, column, piece.getType(), piece.getColor()));
                }
            }
        }
        return new GameSnapshot(pieces, manager.getCurrentTurn(),
                manager.getTurn().getTurnNumber(), manager.getState());
    }
}
