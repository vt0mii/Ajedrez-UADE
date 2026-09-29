package com.uade.ajedrez.domain.model;

public class Jugador {
    private String nombre;
    private int victorias;
    private int derrotas;

    public Jugador(String n) {
        this.nombre = n;
        this.victorias = 0;
        this.derrotas = 0;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getVictorias() {
        return victorias;
    }

    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }
    
}
