package com.uade.ajedrez.domain.model;

public class Square {
    private final Position position;
    private Piece piece;

    public Square(Position position) {
        this.position = position;
        this.piece = null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Position getPosition() {
        return position;
    }
}
