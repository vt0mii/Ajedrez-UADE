package com.uade.ajedrez.domain.model;

public class Jugador {
    private final String nombre;
    private final Color color;

    public Jugador(String nombre, Color color) {
        this.nombre = nombre;
        this.color = color;
    }

    public String getNombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }
}
