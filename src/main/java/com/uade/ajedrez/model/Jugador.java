package main.java.com.uade.ajedrez.model;

public class Jugador {
    private String nombre;
    private int elo;

    public Jugador(String n, int elo) {
        this.nombre = n;
        this.elo = elo;
    }

    public Jugador(String n) {
        this.nombre = n;
        this.elo = 1200;
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getElo() {
        return this.elo;
    }
}
