package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.*;

public class StratRook implements IMovementStrategy {

    @Override
    public MoveResult canMove(Board board, Move move, Piece piece) {
        Position origin = move.getOrigin();
        Position destination = move.getDestination();

        int diffRow = destination.getRow() - origin.getRow();
        int diffColumn = destination.getColumn() - origin.getColumn();

        if (diffColumn != 0 && diffRow != 0) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Rook can only move in straight lines");
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

        if (originColumn == destColumn) {
            int direction = (destRow > originRow) ? 1 : -1;
            for (int row = originRow + direction; row != destRow; row += direction) {
                if (board.getPiece(new Position(row, originColumn)) != null) {
                    return false;
                }
            }
        } else {
            int direction = (destColumn > originColumn) ? 1 : -1;
            for (int column = originColumn + direction; column != destColumn; column += direction) {
                if (board.getPiece(new Position(originRow, column)) != null) {
                    return false;
                }
            }
        }

        return true;
    }
}
