package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.application.dto.GameView;

// Vuelve a guardar la partida. El autoguardado ya existe; esto es para que el
// jugador pueda forzarlo cuando quiera.
public interface SaveGameUseCase {
    GameView saveGame(String gameId);
}
