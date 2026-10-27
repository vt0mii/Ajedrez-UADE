package com.uade.ajedrez.domain.exception;

// Algo falló guardando o leyendo una partida. Guardamos la causa original
// para no perder el detalle.
public class GamePersistenceException extends RuntimeException {
    public GamePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
