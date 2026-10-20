package com.uade.ajedrez.domain.exception;

/** A recoverable error in the text used to enter a move. */
public class InvalidMoveInputException extends IllegalArgumentException {
    public InvalidMoveInputException(String message) {
        super(message);
    }
}
