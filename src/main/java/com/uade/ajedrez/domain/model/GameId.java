package com.uade.ajedrez.domain.model;

import java.util.regex.Pattern;

// Id de la partida. El formato permitido también sirve como nombre de archivo,
// así que no se puede salir del directorio de partidas.
public final class GameId {
    private static final Pattern FORMATO_VALIDO = Pattern.compile("[A-Za-z0-9_-]{1,64}");

    private final String value;

    public GameId(String value) {
        if (value == null || !FORMATO_VALIDO.matcher(value).matches()) {
            throw new IllegalArgumentException("Game id must match [A-Za-z0-9_-]{1,64}");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
