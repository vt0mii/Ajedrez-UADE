package com.uade.ajedrez.infrastructure.adapter.in.http;

import java.util.List;

/**
 * Utilidades mínimas para escribir JSON sin dependencias externas.
 */
final class Json {

    private Json() {
    }

    static String quote(String value) {
        if (value == null) {
            return "null";
        }

        StringBuilder quoted = new StringBuilder("\"");
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            switch (current) {
                case '"' -> quoted.append("\\\"");
                case '\\' -> quoted.append("\\\\");
                case '\n' -> quoted.append("\\n");
                case '\r' -> quoted.append("\\r");
                case '\t' -> quoted.append("\\t");
                default -> {
                    if (current < 0x20) {
                        quoted.append(String.format("\\u%04x", (int) current));
                    } else {
                        quoted.append(current);
                    }
                }
            }
        }
        return quoted.append('"').toString();
    }

    static String stringArray(List<String> values) {
        StringBuilder json = new StringBuilder("[");
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append(quote(values.get(index)));
        }
        return json.append(']').toString();
    }
}
