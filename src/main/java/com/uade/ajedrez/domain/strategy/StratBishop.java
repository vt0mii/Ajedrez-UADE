package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.*;

public class StratBishop implements IMovementStrategy {

    @Override
    public MoveResult canMove(Board board, Move move, Piece piece) {
        Position origin = move.getOrigin();
        Position destination = move.getDestination();

        int diffRow = Math.abs(destination.getRow() - origin.getRow());
        int diffColumn = Math.abs(destination.getColumn() - origin.getColumn());

        if (diffRow != diffColumn) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Bishop can only move diagonally");
        }

        if (!isPathClear(origin, destination, board)) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Path blocked");
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

    private boolean isPathClear(Position origin, Position destination, Board board) {
        int originRow = origin.getRow();
        int destRow = destination.getRow();
        int originColumn = origin.getColumn();
        int destColumn = destination.getColumn();

        int directionRow = (destRow > originRow) ? 1 : -1;
        int directionColumn = (destColumn > originColumn) ? 1 : -1;

        int row = originRow + directionRow;
        int column = originColumn + directionColumn;
        while (row != destRow && column != destColumn) {
            if (board.getPiece(new Position(row, column)) != null) {
                return false;
            }
            row += directionRow;
            column += directionColumn;
        }

        return true;
    }
}
