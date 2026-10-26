package com.uade.ajedrez.application.exception;

import com.uade.ajedrez.domain.model.Reason;

// El dominio rechazó la jugada. Guardamos el motivo para que el adaptador
// HTTP lo convierta en un 409 y el frontend muestre algo útil.
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
