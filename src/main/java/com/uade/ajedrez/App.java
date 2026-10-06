package com.uade.ajedrez;

import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.ValidadorMovimiento;

public class App {
    public static void main(String[] args) {
        Tablero tablero = new Tablero();
        ValidadorMovimiento validador = new ValidadorMovimiento();

        Jugador jugadorBlanco = new Jugador("Tomi", Color.BLANCO);
        Jugador jugadorNegro = new Jugador("JuanDoe", Color.NEGRO);

        PartidaAjedrez partida = new PartidaAjedrez(jugadorBlanco, jugadorNegro, tablero, validador);
        partida.jugarPartida();
    }
}
