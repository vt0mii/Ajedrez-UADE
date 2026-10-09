package com.uade.ajedrez.infrastructure.adapter.in;

import com.uade.ajedrez.domain.model.PartidaAjedrez;
import com.uade.ajedrez.domain.port.in.Entrada;
import com.uade.ajedrez.domain.port.out.Salida;

import java.util.Scanner;

public class ConsolaAdapter implements Entrada, Salida {
    private final Scanner scanner = new Scanner(System.in);
    private final PartidaAjedrez partida;

    public ConsolaAdapter(PartidaAjedrez partida) {
        this.partida = partida;
    }

    public void iniciar() {
        partida.jugarPartida(this, this);
    }

    @Override
    public String leerLinea() {
        return scanner.nextLine();
    }

    @Override
    public void mostrar(String mensaje) {
        System.out.println(mensaje);
    }
}
