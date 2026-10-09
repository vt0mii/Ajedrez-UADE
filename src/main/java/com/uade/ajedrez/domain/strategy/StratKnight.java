package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.*;

public class StratKnight implements IMovementStrategy {

    @Override
    public MoveResult canMove(Board board, Move move, Piece piece) {
        Position origin = move.getOrigin();
        Position destination = move.getDestination();

        int diffRow = Math.abs(destination.getRow() - origin.getRow());
        int diffColumn = Math.abs(destination.getColumn() - origin.getColumn());

        if (!((diffRow == 2 && diffColumn == 1) || (diffRow == 1 && diffColumn == 2))) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Knight can only move in L-shape");
        }

        Piece targetPiece = board.getPiece(destination);
        if (targetPiece == null) {
            return new MoveResult(true, null, "Valid move");
        } else if (targetPiece.getColor() != piece.getColor()) {
            return new MoveResult(true, Reason.CAPTURE, "Valid capture");
        } else {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Cannot capture own piece");
        }
    }
}
