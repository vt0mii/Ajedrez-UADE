package com.uade.ajedrez.application.port.in;

import java.util.List;

public interface LegalMovesUseCase {
    /**
     * @return destinos en notación algebraica a los que puede llegar la pieza
     *         ubicada en {@code fromSquare}, por ejemplo ["e3", "e4"].
     */
    List<String> legalMoves(String gameId, String fromSquare);
}