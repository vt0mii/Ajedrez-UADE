package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.application.service.ReplaySession;

public interface ReplayGameUseCase {
    ReplaySession startReplay(String gameId);
}
