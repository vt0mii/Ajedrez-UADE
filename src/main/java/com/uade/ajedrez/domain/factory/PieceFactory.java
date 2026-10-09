package com.uade.ajedrez.domain.factory;

import com.uade.ajedrez.domain.model.Color;
import com.uade.ajedrez.domain.model.Piece;
import com.uade.ajedrez.domain.model.PieceType;
import com.uade.ajedrez.domain.strategy.*;

public final class PieceFactory {

    private PieceFactory() {
    }

    public static Piece create(PieceType type, Color color) {
        IMovementStrategy strategy = createStrategy(type);
        return new Piece(color, type, strategy);
    }

    private static IMovementStrategy createStrategy(PieceType type) {
        switch (type) {
            case PAWN:
                return new StratPawn();
            case BISHOP:
                return new StratBishop();
            case ROOK:
                return new StratRook();
            case KNIGHT:
                return new StratKnight();
            case QUEEN:
                return new StratQueen();
            case KING:
                return new StratKing();
            default:
                throw new IllegalArgumentException("Unsupported piece type: " + type);
        }
    }
}
