package com.uade.ajedrez.model.factory;

import com.uade.ajedrez.model.Color;
import com.uade.ajedrez.model.Pieza;
import com.uade.ajedrez.model.TipoPieza;
import com.uade.ajedrez.model.strategy.*;

public final class PiezaFactory {
    private PiezaFactory() {

    }

    public static Pieza crear(TipoPieza tipo, Color color) {
        Pieza pieza = new Pieza(color);
        pieza.setTipo(tipo);
        pieza.setEstrategia(crearEstrategia(tipo));
        return pieza;
    }

    private static IEstrategiaMovimiento crearEstrategia(TipoPieza tipo) {
        switch (tipo) {
            case PEON:
                return new EstrategiaPeon();
            case ALFIL:
                return new EstrategiaAlfil();
            case TORRE:
                return new EstrategiaTorre();
            case CABALLO:
                return new EstrategiaCaballo();
            case REINA:
                return new EstrategiaReina();
            case REY:
                return new EstrategiaRey();
            default:
                throw new IllegalArgumentException("Tipo de pieza no soportado: " + tipo);
        }
    }
}
