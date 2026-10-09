package com.uade.ajedrez.domain.model;

public class MoveResult {
    private final boolean wasPossible;
    private final Reason reason;
    private final String message;

    public MoveResult(boolean wasPossible, Reason reason, String message) {
        this.wasPossible = wasPossible;
        this.reason = reason;
        this.message = message;
    }

    public boolean wasPossible() {
        return wasPossible;
    }

    public Reason getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }
}
