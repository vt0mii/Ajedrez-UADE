package com.uade.ajedrez.domain.model;

public class Move {
    private Position origin;
    private Position destination;

    public Move(Position o, Position d) {
        this.origin = o;
        this.destination = d;
    }

    public Position getOrigin() {
        return this.origin;
    }

    public Position getDestination() {
        return this.destination;
    }
}
