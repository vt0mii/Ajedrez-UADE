package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.port.in.Entrada;
import com.uade.ajedrez.domain.port.out.Salida;
import com.uade.ajedrez.domain.service.ValidadorMovimiento;

public class PartidaAjedrez {
    private final Tablero tablero;
    private final ValidadorMovimiento validadorMovimiento;
    private final Turno turno;
    private Estado estado;

    public PartidaAjedrez(Jugador jugadorBlanco, Jugador jugadorNegro, Tablero tablero,
            ValidadorMovimiento validadorMovimiento) {
        this.tablero = tablero;
        this.validadorMovimiento = validadorMovimiento;
        this.turno = new Turno(jugadorBlanco, jugadorNegro);
        this.estado = Estado.EN_CURSO;
    }

    public ResultadoMovimiento realizarMovimiento(Movimiento movimiento) {
        if (estado == Estado.JAQUE_MATE) {
            return new ResultadoMovimiento(false, Motivo.NO_DISPONIBLE, "La partida ya finalizo");
        }

        Color colorEnTurno = turno.getJugadorActual().getColor();
        ResultadoMovimiento validacion = validadorMovimiento.validarMovimiento(tablero, movimiento, colorEnTurno);
        if (!validacion.isFuePosible()) {
            return validacion;
        }

        tablero.moverPieza(movimiento);
        turno.siguienteTurno();

        actualizarEstado();

        return validacion;
    }

    private void actualizarEstado() {
        Color colorEnTurno = turno.getJugadorActual().getColor();
        if (validadorMovimiento.estaEnJaqueMate(tablero, colorEnTurno)) {
            estado = Estado.JAQUE_MATE;
        } else if (validadorMovimiento.estaEnJaque(tablero, colorEnTurno)) {
            estado = Estado.JAQUE;
        } else {
            estado = Estado.EN_CURSO;
        }
    }

    public void jugarPartida(Entrada entrada, Salida salida) {
        while (estado != Estado.JAQUE_MATE) {
            salida.mostrar("\n" + tablero);
            salida.mostrar("Turno #" + turno.getTurnoNumero() + " - " + turno.getJugadorActual().getNombre() + " (" + turno.getJugadorActual().getColor() + ")");
            if (estado == Estado.JAQUE) {
                salida.mostrar("¡JAQUE! El rey esta en peligro.");
            }
            salida.mostrar("Ingrese movimiento (ej: e2 e4) o salir: ");

            String input = entrada.leerLinea().trim();

            if (input.equalsIgnoreCase("salir")) {
                salida.mostrar("Partida abandonada.");
                return;
            }

            try {
                String[] partes = input.split(" ");
                if (partes.length != 2) {
                    salida.mostrar("Formato invalido. Use: origen destino (ej: e2 e4)");
                    continue;
                }

                Posicion origen = parsearCoordenada(partes[0]);
                Posicion destino = parsearCoordenada(partes[1]);

                Movimiento movimiento = new Movimiento(origen, destino);
                ResultadoMovimiento resultado = realizarMovimiento(movimiento);

                if (resultado.isFuePosible()) {
                    salida.mostrar("Movimiento realizado: " + partes[0] + " -> " + partes[1]);
                } else {
                    salida.mostrar("Movimiento invalido: " + resultado.getMensaje());
                }
            } catch (IllegalArgumentException e) {
                salida.mostrar("Error: " + e.getMessage());
            }
        }

        salida.mostrar("\n" + tablero);
        salida.mostrar("¡JAQUE_MATE! Fin de la partida.");
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

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Color getTurnoActual() {
        return turno.getJugadorActual().getColor();
    }

    public Jugador getJugadorEnTurno() {
        return turno.getJugadorActual();
    }

    public Turno getTurno() {
        return turno;
    }

    public Tablero getTablero() {
        return tablero;
    }
}
