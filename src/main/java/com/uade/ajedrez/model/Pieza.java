package main.java.com.uade.ajedrez.model;

import main.java.com.uade.ajedrez.model.strategy.IEstrategiaMovimiento;

public class Pieza {
    private Color color;
    private TipoPieza tipo;
    private IEstrategiaMovimiento estrategia;
    private boolean seMovio;

    public Pieza(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean canMove(Tablero t, Movimiento m) {
        return false;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public TipoPieza getTipo() {
        return tipo;
    }

    public void setTipo(TipoPieza tipo) {
        this.tipo = tipo;
    }

    public IEstrategiaMovimiento getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(IEstrategiaMovimiento estrategia) {
        this.estrategia = estrategia;
    }

    public boolean isSeMovio() {
        return seMovio;
    }

    public void setSeMovio(boolean seMovio) {
        this.seMovio = seMovio;
    }

    
}
