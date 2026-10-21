package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.exception.InvalidMoveInputException;

public class Position {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Convierte esta posición a notación algebraica, por ejemplo fila 6 /
     * columna 4 -> "e2". La fila 0 es el rango 8 y la fila 7 el rango 1.
     */
    public String toAlgebraic() {
        char file = (char) ('a' + column);
        int rank = Board.SIZE - row;
        return String.valueOf(file) + rank;
    }

    public static Position fromAlgebraic(String coordinate) {
        if (coordinate == null || coordinate.length() != 2) {
            throw new InvalidMoveInputException("Invalid coordinate: " + coordinate);
        }

        char file = Character.toLowerCase(coordinate.charAt(0));
        char rank = coordinate.charAt(1);

        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new InvalidMoveInputException("Coordinate out of board: " + coordinate);
        }

        int column = file - 'a';
        int row = Board.SIZE - Character.digit(rank, 10);
        return new Position(row, column);
    }
}
