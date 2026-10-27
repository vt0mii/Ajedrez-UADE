package com.uade.ajedrez.infrastructure.adapter.in.http;

// Lector mínimo de JSON para los bodies de la API: objetos planos con strings.
// Alcanza para este contrato y evita meter Jackson.
final class JsonBody {

    private JsonBody() {
    }

    // Devuelve el valor del campo o null si no está.
    static String stringField(String json, String field) {
        int index = valueStart(json, field);
        if (index < 0) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        for (; index < json.length(); index++) {
            char current = json.charAt(index);
            if (current == '"') {
                return value.toString();
            }
            if (current == '\\') {
                index = appendEscaped(json, index + 1, value);
            } else {
                value.append(current);
            }
        }
        return null;
    }

    // Posición donde empieza el string del campo, o -1 si no existe o no es string.
    private static int valueStart(String json, String field) {
        if (json == null) {
            return -1;
        }

        String quoted = "\"" + field + "\"";
        int index = json.indexOf(quoted);
        if (index < 0) {
            return -1;
        }

        index = skipWhitespace(json, index + quoted.length());
        if (index >= json.length() || json.charAt(index) != ':') {
            return -1;
        }

        index = skipWhitespace(json, index + 1);
        if (index >= json.length() || json.charAt(index) != '"') {
            return -1;
        }
        return index + 1;
    }

    private static int appendEscaped(String json, int index, StringBuilder value) {
        if (index >= json.length()) {
            return index;
        }

        char escaped = json.charAt(index);
        switch (escaped) {
            case 'n' -> value.append('\n');
            case 'r' -> value.append('\r');
            case 't' -> value.append('\t');
            case 'b' -> value.append('\b');
            case 'f' -> value.append('\f');
            case 'u' -> {
                if (index + 4 < json.length()) {
                    value.append((char) Integer.parseInt(json.substring(index + 1, index + 5), 16));
                    return index + 4;
                }
            }
            default -> value.append(escaped);
        }
        return index;
    }

    private static int skipWhitespace(String json, int index) {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
        return index;
    }
}
