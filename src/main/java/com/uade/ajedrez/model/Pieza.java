package main.java.com.uade.ajedrez.model;

public abstract class Pieza {
    private Color color;

    public Pieza(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean canMove(Tablero t, Movimiento m) {
        return false;
    }
}
