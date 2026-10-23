package com.uade.ajedrez.domain.model;

import java.util.List;

/** Immutable view of a game. Contains no transport or UI types. */
public record GameSnapshot(List<SquareView> pieces, Color currentTurn, int turnNumber, State state) {
    public GameSnapshot {
        pieces = List.copyOf(pieces);
    }

    public record SquareView(int row, int column, PieceType type, Color color) { }
}
