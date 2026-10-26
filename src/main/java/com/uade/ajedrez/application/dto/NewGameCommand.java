package com.uade.ajedrez.application.dto;

// Datos para arrancar una partida. Los nombres los pone la interfaz.
public record NewGameCommand(String whitePlayerName, String blackPlayerName) {
}
