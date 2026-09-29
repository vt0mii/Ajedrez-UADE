package com.uade.ajedrez.domain.factory;

import com.uade.ajedrez.domain.model.Color;
import com.uade.ajedrez.domain.model.Pieza;
import com.uade.ajedrez.domain.model.TipoPieza;
import com.uade.ajedrez.domain.strategy.*;

public final class PiezaFactory {

    private PiezaFactory() {
    }

    public static Pieza crear(TipoPieza tipo, Color color) {
        IEstrategiaMovimiento estrategia = crearEstrategia(tipo);
        return new Pieza(color, tipo, estrategia);
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
