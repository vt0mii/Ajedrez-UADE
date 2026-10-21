package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.application.dto.GameView;

public interface AbandonGameUseCase {
    GameView abandonGame(String gameId);
}
