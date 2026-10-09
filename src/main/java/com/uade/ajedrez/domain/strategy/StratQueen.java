package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.*;

public class StratQueen implements IMovementStrategy {

    private final IMovementStrategy rookStrategy = new StratRook();
    private final IMovementStrategy bishopStrategy = new StratBishop();

    @Override
    public MoveResult canMove(Board board, Move move, Piece piece) {
        Position origin = move.getOrigin();
        Position destination = move.getDestination();

        int diffRow = Math.abs(destination.getRow() - origin.getRow());
        int diffColumn = Math.abs(destination.getColumn() - origin.getColumn());

        if (diffRow == 0 || diffColumn == 0) {
            return rookStrategy.canMove(board, move, piece);
        }

        if (diffRow == diffColumn) {
            return bishopStrategy.canMove(board, move, piece);
        }

        return new MoveResult(false, Reason.NOT_AVAILABLE, "Queen can only move in straight lines or diagonally");
    }
}
