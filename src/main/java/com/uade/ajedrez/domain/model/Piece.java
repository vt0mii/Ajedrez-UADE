package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.strategy.IMovementStrategy;

public class Piece {
    private final Color color;
    private final PieceType type;
    private final IMovementStrategy strategy;

    public Piece(Color color, PieceType type, IMovementStrategy strategy) {
        this.color = color;
        this.type = type;
        this.strategy = strategy;
    }

    public Color getColor() {
        return this.color;
    }

    public PieceType getType() {
        return this.type;
    }

    public IMovementStrategy getStrategy() {
        return this.strategy;
    }

    public MoveResult canMove(Board board, Move move) {
        return this.strategy.canMove(board, move, this);
    }
}
