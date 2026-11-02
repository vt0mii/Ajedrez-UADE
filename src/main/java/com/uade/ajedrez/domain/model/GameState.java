package com.uade.ajedrez.domain.model;

public enum GameState {
    IN_PROGRESS,
    CHECK,
    CHECKMATE,
    ABANDONED,
    DRAW;

    public boolean isFinished() {
        return this == CHECKMATE || this == ABANDONED || this == DRAW;
    }
}