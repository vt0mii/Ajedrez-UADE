package com.uade.ajedrez.domain.model;

public class ResultadoMovimiento {
    private final boolean fuePosible;
    private final Motivo motivo;
    private final String mensaje;

    public ResultadoMovimiento(boolean fuePosible, Motivo motivo, String mensaje) {
        this.fuePosible = fuePosible;
        this.motivo = motivo;
        this.mensaje = mensaje;
    }

    public boolean isFuePosible() {
        return fuePosible;
    }

    public Motivo getMotivo() {
        return motivo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
