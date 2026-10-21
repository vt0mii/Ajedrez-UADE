package com.uade.ajedrez.domain.exception;

/** Falló el guardado o la carga; conserva la causa original para diagnóstico. */
public class GamePersistenceException extends RuntimeException {
    public GamePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
