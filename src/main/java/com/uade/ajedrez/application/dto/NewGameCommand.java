package com.uade.ajedrez.application.dto;

/**
 * Comando para iniciar una partida. Los nombres de los jugadores los provee la
 * interfaz de usuario.
 */
public record NewGameCommand(String whitePlayerName, String blackPlayerName) {
}
