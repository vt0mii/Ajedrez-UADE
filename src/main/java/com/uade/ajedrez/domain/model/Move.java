package com.uade.ajedrez.domain.model;

public class Move {
    private final Position origin;
    private final Position destination;
    // Solo la usa el peon cuando llega a la ultima fila. En el resto de las
    // jugadas queda en null.
    private final PieceType promotion;

    public Move(Position origin, Position destination) {
        this(origin, destination, null);
    }

    public Move(Position origin, Position destination, PieceType promotion) {
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("A move requires an origin and a destination");
        }
        this.origin = origin;
        this.destination = destination;
        this.promotion = promotion;
    }

    public Position getOrigin() {
        return origin;
    }

    public Position getDestination() {
        return destination;
    }

    public PieceType getPromotion() {
        return promotion;
    }

    // Una promocion sin pieza elegida se resuelve como reina, que es lo que
    // hace la mayoria de las interfaces cuando no se pregunta nada.
    public PieceType promotionOrQueen() {
        return promotion == null ? PieceType.QUEEN : promotion;
    }

    public boolean reachesLastRank(Color color) {
        int lastRow = color == Color.WHITE ? 0 : Board.SIZE - 1;
        return destination.getRow() == lastRow;
    }
}
