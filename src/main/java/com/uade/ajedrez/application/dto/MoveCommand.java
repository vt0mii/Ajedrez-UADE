package com.uade.ajedrez.application.dto;

// Movimiento tal como llega desde afuera ("e2" a "e4"). Vive en aplicación
// para que el dominio no se entere de consola ni de HTTP.
public record MoveCommand(String from, String to) {
}
