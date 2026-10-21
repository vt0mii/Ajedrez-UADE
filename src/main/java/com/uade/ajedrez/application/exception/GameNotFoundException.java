package com.uade.ajedrez.application.exception;

public class GameNotFoundException extends RuntimeException {
    private final String gameId;

    public GameNotFoundException(String gameId) {
        super("Game not found: " + gameId);
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
