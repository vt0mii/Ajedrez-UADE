package main.java.com.uade.ajedrez.model;

public class PartidaAjedrez {
    private Jugador jugadorBlanco;
    private Jugador jugadorNegro;
    private Estado estado;
    private Color turnoActual;

    public PartidaAjedrez(Jugador jugadorBlanco, Jugador jugadorNegro, Estado estado) {
        this.jugadorBlanco = jugadorBlanco;
        this.jugadorNegro = jugadorNegro;
        this.estado = Estado.EN_CURSO;
        this.turnoActual = Color.BLANCO;
    }

    public boolean realizarMovimiento(Movimiento movimiento) {
        return false;
    }
}
