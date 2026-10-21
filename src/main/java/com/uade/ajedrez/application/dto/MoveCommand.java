package com.uade.ajedrez.application.dto;

/**
 * Comando para mover una pieza usando coordenadas algebraicas, por ejemplo de
 * "e2" a "e4". El formato de transporte queda en la aplicación para no filtrar
 * detalles de consola o HTTP al dominio.
 */
public record MoveCommand(String from, String to) {
}
