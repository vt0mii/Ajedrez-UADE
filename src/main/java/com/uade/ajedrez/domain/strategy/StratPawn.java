package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.*;

public class StratPawn implements IMovementStrategy {

    @Override
    public MoveResult canMove(Board board, Move move, Piece piece) {
        Position origin = move.getOrigin();
        Position destination = move.getDestination();

        int direction = (piece.getColor() == Color.WHITE) ? -1 : 1;
        int initialRow = (piece.getColor() == Color.WHITE) ? 6 : 1;

        int diffRow = destination.getRow() - origin.getRow();
        int diffColumn = Math.abs(destination.getColumn() - origin.getColumn());

        // Simple advance
        if (diffRow == direction && diffColumn == 0) {
            if (board.getPiece(destination) == null) {
                return new MoveResult(true, null, "Valid move");
            } else {
                return new MoveResult(false, Reason.NOT_AVAILABLE, "Path blocked");
            }
        }

        // Double advance
        if (origin.getRow() == initialRow && diffRow == (2 * direction) && diffColumn == 0) {
            Position intermediate = new Position(origin.getRow() + direction, origin.getColumn());
            if (board.getPiece(intermediate) == null && board.getPiece(destination) == null) {
                return new MoveResult(true, null, "Valid move");
            } else {
                return new MoveResult(false, Reason.NOT_AVAILABLE, "Path blocked");
            }
        }

        // Capture
        if (diffRow == direction && diffColumn == 1) {
            Piece targetPiece = board.getPiece(destination);
            if (targetPiece != null && targetPiece.getColor() != piece.getColor()) {
                return new MoveResult(true, Reason.CAPTURE, "Valid capture");
            }
        }

        return new MoveResult(false, Reason.NOT_AVAILABLE, "Invalid move for pawn");
    }
}
