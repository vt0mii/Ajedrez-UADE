package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.application.dto.ReplayView;
import com.uade.ajedrez.application.service.ReplaySession;

public interface ReplayGameUseCase {
    ReplaySession startReplay(String gameId);

    // Versión sin sesión para el adaptador HTTP: devuelve la posición pedida
    // dentro del historial, sin guardar nada.
    ReplayView replayAt(String gameId, int position);
}
