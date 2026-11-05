package com.uade.ajedrez.application.dto;

// Movimiento tal como llega desde afuera ("e2" a "e4"). Vive en aplicacion
// para que el dominio no se entere de consola ni de HTTP.
// "promotion" es opcional: solo viene cuando un peon llega a la ultima fila.
public record MoveCommand(String from, String to, String promotion) {

    public MoveCommand(String from, String to) {
        this(from, to, null);
    }
}
