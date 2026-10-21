package com.uade.ajedrez.domain.exception;

/** Error recuperable en el texto usado para ingresar un movimiento. */
public class InvalidMoveInputException extends IllegalArgumentException {
    public InvalidMoveInputException(String message) {
        super(message);
    }
}
