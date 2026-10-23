package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.exception.InvalidMoveInputException;

public class Position {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /** Converts chess notation (a1-h8) to board coordinates for any adapter. */
    public static Position fromAlgebraic(String coordinate) {
        if (coordinate == null || coordinate.length() != 2) {
            throw new InvalidMoveInputException("Invalid coordinate: " + coordinate);
        }
        int column = "abcdefgh".indexOf(coordinate.charAt(0));
        int row = "87654321".indexOf(coordinate.charAt(1));
        if (row < 0 || column < 0) {
            throw new InvalidMoveInputException("Coordinate out of board: " + coordinate);
        }
        return new Position(row, column);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
