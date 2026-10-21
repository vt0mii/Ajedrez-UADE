package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.NewGameCommand;

public interface CreateGameUseCase {
    GameView createGame(NewGameCommand command);
}
