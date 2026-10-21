package com.uade.ajedrez.application.exception;

/**
 * Se lanza cuando la entrada no se puede interpretar (id de partida inválido,
 * coordenadas incorrectas, nombres vacíos, etc.). Se separa de las violaciones
 * de reglas a propósito.
 */
public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
