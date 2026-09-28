package com.uade.ajedrez.model;

public class ResultadoMovimiento {
    private boolean fuePosible;
    private Motivo motivo;
    private String mensaje;

    public ResultadoMovimiento(boolean fuePosible, Motivo motivo, String mensaje) {
        this.fuePosible = fuePosible;
        this.motivo = motivo;
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Motivo getMotivo() {
        return motivo;
    }

    public boolean isFuePosible() {
        return fuePosible;
    }

    public void setFuePosible(boolean fuePosible) {
        this.fuePosible = fuePosible;
    }

    public void setMotivo(Motivo motivo) {
        this.motivo = motivo;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    
}
