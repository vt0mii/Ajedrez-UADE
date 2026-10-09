package com.uade.ajedrez;

import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.ValidadorMovimiento;
import com.uade.ajedrez.infrastructure.adapter.in.ConsolaAdapter;
import com.uade.ajedrez.infrastructure.adapter.out.MemoriaPartida;

public class App {
    public static void main(String[] args) {
        // Composición raíz
        Tablero tablero = new Tablero();
        ValidadorMovimiento validador = new ValidadorMovimiento();

        Jugador jugadorBlanco = new Jugador("Tomi", Color.BLANCO);
        Jugador jugadorNegro = new Jugador("JuanDoe", Color.NEGRO);

        PartidaAjedrez partida = new PartidaAjedrez(jugadorBlanco, jugadorNegro, tablero, validador);
        MemoriaPartida persistencia = new MemoriaPartida();
        ConsolaAdapter consola = new ConsolaAdapter(partida);

        consola.iniciar();
    }
}
