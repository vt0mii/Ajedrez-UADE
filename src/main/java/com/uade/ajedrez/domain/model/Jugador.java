package com.uade.ajedrez.domain.model;

public class Jugador {
    private String nombre;
    private Color color;
    private int victorias;
    private int derrotas;

    public Jugador(String n, Color color) {
        this.nombre = n;
        this.color = color;
        this.victorias = 0;
        this.derrotas = 0;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
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
