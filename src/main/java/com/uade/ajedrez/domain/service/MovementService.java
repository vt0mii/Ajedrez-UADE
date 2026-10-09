package com.uade.ajedrez.domain.service;

import com.uade.ajedrez.domain.model.*;

public class MovementService {

    public MoveResult validateOriginPiece(Piece piece, ChessManager game) {
        if (piece == null) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "No piece at origin position");
        }

        if (piece.getColor() != game.getCurrentTurn()) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "Not that piece's turn");
        }

        return new MoveResult(true, null, "Valid move");
    }

    public MoveResult validateMove(Board board, Move move, Color playerColor) {
        Piece piece = board.getPiece(move.getOrigin());
        if (piece == null) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "No piece at origin position");
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

    private boolean leavesKingInCheck(Board board, Move move, Color playerColor) {
        Board simulatedBoard = simulateMove(board, move);
        return isInCheck(simulatedBoard, playerColor);
    }

    private Board simulateMove(Board board, Move move) {
        Board copy = new Board();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = board.getPiece(new Position(row, column));
                if (piece != null) {
                    copy.getSquare(new Position(row, column)).setPiece(piece);
                }
            }
        }
        copy.movePiece(move);
        return copy;
    }

    public boolean isInCheck(Board board, Color kingColor) {
        Position kingPosition = findKing(board, kingColor);
        if (kingPosition == null) {
            return false;
        }

        Color enemyColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;

        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = board.getPiece(new Position(row, column));
                if (piece != null && piece.getColor() == enemyColor) {
                    Move attackMove = new Move(
                            new Position(row, column),
                            kingPosition
                    );
                    MoveResult result = piece.canMove(board, attackMove);
                    if (result.wasPossible()) {
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

        return !hasValidMove(board, playerColor);
    }

    private boolean hasValidMove(Board board, Color playerColor) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = board.getPiece(new Position(row, column));
                if (piece != null && piece.getColor() == playerColor) {
                    if (pieceCanMove(board, piece, new Position(row, column))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean pieceCanMove(Board board, Piece piece, Position origin) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Position destination = new Position(row, column);
                Move move = new Move(origin, destination);
                MoveResult result = piece.canMove(board, move);
                if (result.wasPossible()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Position findKing(Board board, Color color) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = board.getPiece(new Position(row, column));
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == color) {
                    return new Position(row, column);
                }
            }
        }
        return null;
    }
}
