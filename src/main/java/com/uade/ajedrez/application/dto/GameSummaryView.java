package com.uade.ajedrez.application.dto;

import com.uade.ajedrez.domain.model.Color;
import com.uade.ajedrez.domain.model.GameState;

// Resumen de una partida guardada para el listado del lobby. No lleva el
// tablero: con esto alcanza para elegir cuál abrir.
public record GameSummaryView(
        String id,
        GameState status,
        Color turn,
        int turnNumber,
        String whitePlayer,
        String blackPlayer,
        int moveCount,
        GameView.MoveView lastMove,
        long updatedAt) {
}
