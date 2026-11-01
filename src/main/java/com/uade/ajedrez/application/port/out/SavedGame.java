package com.uade.ajedrez.application.port.out;

import com.uade.ajedrez.domain.model.ChessGame;

// Partida guardada y cuándo se tocó por última vez. La fecha la pone el
// repositorio, que es el que sabe cuándo cambió el archivo.
public record SavedGame(ChessGame game, long updatedAt) {
}
