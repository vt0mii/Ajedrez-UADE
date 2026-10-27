package com.uade.ajedrez.application.port.in;

import java.util.List;

public interface LegalMovesUseCase {
    // Devuelve las casillas a las que puede ir la pieza de "fromSquare", por
    // ejemplo ["e3", "e4"]. Se usa para pintar los puntitos del tablero.
    List<String> legalMoves(String gameId, String fromSquare);
}