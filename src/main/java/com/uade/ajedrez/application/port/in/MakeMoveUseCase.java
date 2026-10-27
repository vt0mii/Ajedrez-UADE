package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.MoveCommand;

public interface MakeMoveUseCase {
    // Aplica una jugada y devuelve cómo quedó la partida.
    // Tira MoveRejectedException si la jugada no es legal y GameNotFoundException
    // si el id no existe.
    GameView makeMove(String gameId, MoveCommand command);
}
