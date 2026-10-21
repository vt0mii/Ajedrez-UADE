package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.MoveCommand;

public interface MakeMoveUseCase {
    /**
     * Aplica un movimiento y devuelve el nuevo estado de la partida.
     *
     * @throws com.uade.ajedrez.application.exception.MoveRejectedException
     *         cuando el movimiento no es legal
     * @throws com.uade.ajedrez.application.exception.GameNotFoundException
     *         cuando la partida no existe
     */
    GameView makeMove(String gameId, MoveCommand command);
}
