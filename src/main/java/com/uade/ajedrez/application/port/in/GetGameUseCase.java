package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.application.dto.GameView;

public interface GetGameUseCase {
    GameView getGame(String gameId);
}
