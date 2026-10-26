package com.uade.ajedrez.application.dto;

import com.uade.ajedrez.domain.model.Color;
import com.uade.ajedrez.domain.model.GameState;
import com.uade.ajedrez.domain.model.PieceType;

import java.util.List;

// Lo que devuelven los casos de uso. Es el mismo objeto para la consola y para
// el JSON de la API, así no hay dos modelos dando vueltas.
public record GameView(
        String id,
        GameState status,
        Color turn,
        int turnNumber,
        PlayerView whitePlayer,
        PlayerView blackPlayer,
        PieceView[][] board,
        List<MoveView> history,
        MoveView lastMove,
        Color winner) {

    public record PlayerView(String name, Color color) {
    }

    public record PieceView(PieceType type, Color color) {
    }

    public record MoveView(String from, String to) {
    }
}