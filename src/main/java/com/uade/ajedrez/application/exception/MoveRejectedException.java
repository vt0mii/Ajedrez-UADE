package com.uade.ajedrez.application.exception;

import com.uade.ajedrez.domain.model.Reason;

/**
 * Se lanza cuando el dominio rechaza un movimiento. El motivo es un código
 * legible por la máquina para que un adaptador web lo traduzca a un estado HTTP
 * y a un mensaje de error.
 */
public class MoveRejectedException extends RuntimeException {
    private final Reason reason;

    public MoveRejectedException(Reason reason, String detail) {
        super(detail);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
