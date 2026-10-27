package com.uade.ajedrez.application.exception;

// Entrada que no se puede interpretar: id inválido, coordenada mal escrita,
// nombre vacío, etc. Es distinto de una jugada que rompe las reglas.
public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
