package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.strategy.IEstrategiaMovimiento;

public class Pieza {
    private Color color;
    private TipoPieza tipo;
    private IEstrategiaMovimiento estrategia;
    private boolean seMovio;

    public Pieza(Color color, TipoPieza tipo, IEstrategiaMovimiento estrategia) {
        this.color = color;
        this.tipo = tipo;
        this.estrategia = estrategia;
    }
    public Color getColor() {
        return this.color;
    }

    public TipoPieza getTipo() {
        return this.tipo;
    }
    public IEstrategiaMovimiento getEstrategia(){
        return this.estrategia;
    }
    public ResultadoMovimiento canMove(Tablero t, Movimiento m) {
       return this.estrategia.canMove(t, m, this);
    }
    public boolean isSeMovio() {
        return seMovio;
    }

    public void setSeMovio(boolean seMovio) {
        this.seMovio = seMovio;
    }

}
