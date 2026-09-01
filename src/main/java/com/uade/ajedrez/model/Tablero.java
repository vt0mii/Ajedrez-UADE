package main.java.com.uade.ajedrez.model;

import java.util.ArrayList;

public class Tablero {
    private ArrayList<Casilla> casillas;

    public Tablero() {
        this.casillas = new ArrayList<Casilla>();
    }

    public ArrayList<Casilla> getCasillas() {
        return this.casillas;
    }

    public Pieza obtenerPieza(Posicion p) {
        return null;
    }
}
