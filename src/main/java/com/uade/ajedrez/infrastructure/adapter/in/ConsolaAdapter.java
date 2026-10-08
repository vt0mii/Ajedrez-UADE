package com.uade.ajedrez.infrastructure.adapter.in;

import com.uade.ajedrez.application.port.in.IJugarPartidaPort;
import com.uade.ajedrez.domain.model.*;

import java.util.Scanner;

public class ConsolaAdapter {
    private final IJugarPartidaPort jugarPartida;

    public ConsolaAdapter(IJugarPartidaPort jugarPartida) {
        this.jugarPartida = jugarPartida;
    }

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);

        while (jugarPartida.obtenerEstado() == Estado.EN_CURSO) {
            mostrarEstado();

            System.out.print("Ingrese movimiento (ej: e2 e4) o salir: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("salir")) {
                System.out.println("Partida abandonada.");
                scanner.close();
                return;
            }

            procesarMovimiento(input);
        }

        mostrarEstado();
        System.out.println("Fin de la partida. Estado: " + jugarPartida.obtenerEstado());
        scanner.close();
    }

    private void mostrarEstado() {
        System.out.println("\n" + jugarPartida.obtenerTablero());
        System.out.println("Turno #" + jugarPartida.obtenerTurno() + " - " + jugarPartida.obtenerJugadorEnTurno().getNombre() + " (" + jugarPartida.obtenerJugadorEnTurno().getColor() + ")");
    }

    private void procesarMovimiento(String input) {
        try {
            String[] partes = input.split(" ");
            if (partes.length != 2) {
                System.out.println("Formato invalido. Use: origen destino (ej: e2 e4)");
                return;
            }

            Posicion origen = parsearCoordenada(partes[0]);
            Posicion destino = parsearCoordenada(partes[1]);

            Movimiento movimiento = new Movimiento(origen, destino);
            ResultadoMovimiento resultado = jugarPartida.mover(movimiento);

            if (resultado.isFuePosible()) {
                System.out.println("Movimiento realizado: " + partes[0] + " -> " + partes[1]);
            } else {
                System.out.println("Movimiento invalido: " + resultado.getMensaje());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Posicion parsearCoordenada(String coord) {
        if (coord.length() != 2) {
            throw new IllegalArgumentException("Coordenada invalida: " + coord);
        }
        String letras = "abcdefgh";
        String numeros = "87654321";

        int columna = letras.indexOf(coord.charAt(0));
        int fila = numeros.indexOf(coord.charAt(1));

        if (fila < 0 || fila >= 8 || columna < 0 || columna >= 8) {
            throw new IllegalArgumentException("Coordenada fuera del tablero: " + coord);
        }

        return new Posicion(fila, columna);
    }
}
