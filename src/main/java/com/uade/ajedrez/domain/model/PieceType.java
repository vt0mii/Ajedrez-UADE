package com.uade.ajedrez.domain.model;

public enum PieceType {
    PAWN,
    BISHOP,
    ROOK,
    KNIGHT,
    QUEEN,
    KING;

    // Piezas a las que puede ascender un peon. El rey y el propio peon no
    // cuentan.
    public boolean isPromotionPiece() {
        return this == QUEEN || this == ROOK || this == BISHOP || this == KNIGHT;
    }
}
