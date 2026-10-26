package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.factory.PieceFactory;

public class Board {
    public static final int SIZE = 8;

    private final Square[][] squares = new Square[SIZE][SIZE];

    public Board() {
        this(true);
    }

    private Board(boolean withInitialSetup) {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                squares[row][column] = new Square(new Position(row, column));
            }
        }
        if (withInitialSetup) {
            generateBoard();
        }
    }

    // Tablero sin piezas. Sirve para cargar una partida guardada o para armar
    // una posición a mano en los tests.
    public static Board empty() {
        return new Board(false);
    }

    private void generateBoard() {
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

    // Copia el tablero. Las piezas no cambian, así que se comparten las mismas
    // referencias; por eso simular movimientos sale barato.
    public Board copy() {
        Board copy = Board.empty();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                copy.squares[row][column].setPiece(squares[row][column].getPiece());
            }
        }
        return copy;
    }

    private void placePiece(int row, int column, Color color, PieceType type) {
        squares[row][column].setPiece(PieceFactory.create(type, color));
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
