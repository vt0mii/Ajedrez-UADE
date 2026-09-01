package main.java.com.uade.ajedrez.model;

public class Casilla {
    private Pieza pieza;

    public Casilla(Pieza pieza) {
        this.pieza = pieza;
    }

    public Casilla() {
        this.pieza = null;
    }

    public Pieza getPieza() {
        return this.pieza;
    }
}
