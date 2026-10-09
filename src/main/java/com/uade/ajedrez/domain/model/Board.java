package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.factory.PieceFactory;

public class Board {
    public static final int SIZE = 8;

    private final Square[][] squares = new Square[SIZE][SIZE];

    public Board() {
        generateBoard();
    }

    public void generateBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                squares[row][column] = new Square(new Position(row, column));
            }
        }

        PieceType[] initialOrder = {
            PieceType.ROOK,
            PieceType.KNIGHT,
            PieceType.BISHOP,
            PieceType.QUEEN,
            PieceType.KING,
            PieceType.BISHOP,
            PieceType.KNIGHT,
            PieceType.ROOK
        };

        for (int column = 0; column < SIZE; column++) {
            placePiece(0, column, Color.BLACK, initialOrder[column]);
            placePiece(1, column, Color.BLACK, PieceType.PAWN);
            placePiece(6, column, Color.WHITE, PieceType.PAWN);
            placePiece(7, column, Color.WHITE, initialOrder[column]);
        }
    }

    public Square getSquare(Position position) {
        validatePosition(position);
        return squares[position.getRow()][position.getColumn()];
    }

    public Piece getPiece(Position position) {
        return getSquare(position).getPiece();
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        board.append("  a b c d e f g h\n");
        for (int row = 0; row < SIZE; row++) {
            board.append(SIZE - row).append(' ');
            for (int column = 0; column < SIZE; column++) {
                Piece piece = squares[row][column].getPiece();
                board.append(piece == null ? '.' : pieceSymbol(piece));
                if (column < SIZE - 1) {
                    board.append(' ');
                }
            }
            board.append(' ').append(SIZE - row).append('\n');
        }
        board.append("  a b c d e f g h");
        return board.toString();
    }

    private void placePiece(int row, int column, Color color, PieceType type) {
        squares[row][column].setPiece(PieceFactory.create(type, color));
    }

    private char pieceSymbol(Piece piece) {
        char symbol = piece.getType().name().charAt(0);
        if (piece.getType() == PieceType.QUEEN) {
            symbol = 'Q';
        }
        return piece.getColor() == Color.BLACK ? Character.toLowerCase(symbol) : symbol;
    }

    private void validatePosition(Position position) {
        if (position == null
                || position.getRow() < 0 || position.getRow() >= SIZE
                || position.getColumn() < 0 || position.getColumn() >= SIZE) {
            throw new IllegalArgumentException("Position must be within the board (0-7).");
        }
    }

    public void movePiece(Move move) {
        Square origin = getSquare(move.getOrigin());
        Square destination = getSquare(move.getDestination());
        destination.setPiece(origin.getPiece());
        origin.setPiece(null);
    }
}
