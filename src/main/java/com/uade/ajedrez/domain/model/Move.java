package com.uade.ajedrez.domain.model;

public class Move {
    private final Position origin;
    private final Position destination;

    public Move(Position origin, Position destination) {
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("A move requires an origin and a destination");
        }
        this.origin = origin;
        this.destination = destination;
    }

    public Position getOrigin() {
        return origin;
    }

    public Position getDestination() {
        return destination;
    }
}
