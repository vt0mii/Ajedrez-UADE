package com.uade.ajedrez.application.port.in;

import com.uade.ajedrez.application.dto.GameSummaryView;

import java.util.List;

// Lista las partidas guardadas para poder retomarlas desde la interfaz.
public interface ListGamesUseCase {
    List<GameSummaryView> listGames();
}
