package com.uade.ajedrez.domain.exception;

/** Saving or loading failed; retains the original cause for diagnostics. */
public class GamePersistenceException extends RuntimeException {
    public GamePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
