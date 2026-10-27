package com.uade.ajedrez.domain.service;

import com.uade.ajedrez.domain.model.*;

public class MovementService {

    public MoveResult validateMove(Board board, Move move, Color playerColor) {
        Position origin = move.getOrigin();
        Position destination = move.getDestination();
        if (origin.getRow() == destination.getRow() && origin.getColumn() == destination.getColumn()) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Origin and destination are the same");
        }

        Piece piece = board.getPiece(origin);
        if (piece == null) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "No piece at origin position");
        }

        if (piece.getColor() != playerColor) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Cant move enemy piece");
        }

        Piece targetPiece = board.getPiece(move.getDestination());
        if (targetPiece != null && targetPiece.getType() == PieceType.KING) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Cannot capture the king");
        }

        MoveResult result = piece.canMove(board, move);
        if (!result.wasPossible()) {
            return result;
        }

        if (leavesKingInCheck(board, move, playerColor)) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Move leaves king in check");
        }

        return new MoveResult(true, null, "Valid move");
    }

    public boolean isInCheck(Board board, Color kingColor) {
        Position kingPosition = findKing(board, kingColor);
        if (kingPosition == null) {
            return false;
        }

        Color enemyColor = opposite(kingColor);

        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = board.getPiece(new Position(row, column));
                if (piece != null && piece.getColor() == enemyColor) {
                    Move attackMove = new Move(new Position(row, column), kingPosition);
                    if (piece.canMove(board, attackMove).wasPossible()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isCheckmate(Board board, Color playerColor) {
        if (!isInCheck(board, playerColor)) {
            return false;
        }
        return !hasAnyLegalMove(board, playerColor);
    }

    private boolean leavesKingInCheck(Board board, Move move, Color playerColor) {
        Board simulatedBoard = simulateMove(board, move);
        return isInCheck(simulatedBoard, playerColor);
    }

    private Board simulateMove(Board board, Move move) {
        Board copy = board.copy();
        copy.movePiece(move);
        return copy;
    }

    // Usa la validación completa, que incluye el filtro de jaque. Si no, una
    // jugada pseudo-legal podría tapar un mate.
    private boolean hasAnyLegalMove(Board board, Color playerColor) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Position origin = new Position(row, column);
                Piece piece = board.getPiece(origin);
                if (piece != null && piece.getColor() == playerColor) {
                    if (pieceHasLegalMove(board, origin, playerColor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean pieceHasLegalMove(Board board, Position origin, Color playerColor) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Move move = new Move(origin, new Position(row, column));
                if (validateMove(board, move, playerColor).wasPossible()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Position findKing(Board board, Color color) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Position position = new Position(row, column);
                Piece piece = board.getPiece(position);
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == color) {
                    return position;
                }
            }
        }
        return null;
    }

    private Color opposite(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }
}
