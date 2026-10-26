package com.uade.ajedrez.domain.exception;

// Error de tipeo o formato en una jugada. Se puede volver a intentar.
public class InvalidMoveInputException extends IllegalArgumentException {
    public InvalidMoveInputException(String message) {
        super(message);
    }
}
