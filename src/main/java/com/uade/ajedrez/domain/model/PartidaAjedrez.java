package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.service.ValidadorMovimiento;

import java.util.Scanner;

public class PartidaAjedrez {
    private final Tablero tablero;
    private final ValidadorMovimiento validadorMovimiento;
    private final Turnera turnera;
    private Estado estado;

    public PartidaAjedrez(Jugador jugadorBlanco, Jugador jugadorNegro, Tablero tablero,
            ValidadorMovimiento validadorMovimiento) {
        this.tablero = tablero;
        this.validadorMovimiento = validadorMovimiento;
        this.turnera = new Turnera(jugadorBlanco, jugadorNegro);
        this.estado = Estado.EN_CURSO;
    }

    public ResultadoMovimiento realizarMovimiento(Movimiento movimiento) {
        if (estado == Estado.JAQUE_MATE || estado == Estado.TABLAS) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "La partida ya finalizo");
        }

        Pieza pieza = tablero.obtenerPieza(movimiento.getOrigen());
        ResultadoMovimiento validacion = validadorMovimiento.validarPiezaOrigen(pieza, this);
        if (!validacion.isFuePosible()) {
            return validacion;
        }

        ResultadoMovimiento resultado = pieza.canMove(tablero, movimiento);
        if (!resultado.isFuePosible()) {
            return resultado;
        }

        tablero.moverPieza(movimiento);
        turnera.siguienteTurno();

        return resultado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Color getTurnoActual() {
        return turnera.getJugadorActual().getColor();
    }

    public Jugador getJugadorEnTurno() {
        return turnera.getJugadorActual();
    }

    public Turnera getTurnera() {
        return turnera;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void jugarPartida() {
        Scanner scanner = new Scanner(System.in);

        while (estado == Estado.EN_CURSO) { // Faltaria controlar el resto de estados
            System.out.println("\n" + tablero);
            System.out.println("Turno #" + turnera.getTurnoNumero() + " - " + turnera.getJugadorActual().getNombre() + " (" + turnera.getJugadorActual().getColor() + ")");
            System.out.print("Ingrese movimiento (ej: e2 e4) o salir: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("salir")) {
                System.out.println("Partida abandonada.");
                return;
            }

            try {
                String[] partes = input.split(" ");
                if (partes.length != 2) {
                    System.out.println("Formato invalido. Use: origen destino (ej: e2 e4)");
                    continue;
                }

                Posicion origen = parsearCoordenada(partes[0]);
                Posicion destino = parsearCoordenada(partes[1]);

                Movimiento movimiento = new Movimiento(origen, destino);
                ResultadoMovimiento resultado = realizarMovimiento(movimiento);

                if (resultado.isFuePosible()) {
                    System.out.println("Movimiento realizado: " + partes[0] + " -> " + partes[1]);
                } else {
                    System.out.println("Movimiento invalido: " + resultado.getMensaje());
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("\n" + tablero);
        System.out.println("Fin de la partida. Estado: " + estado);
        scanner.close();
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
