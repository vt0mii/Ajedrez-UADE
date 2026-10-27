package com.uade.ajedrez.domain.port.out;

// Contrato de logs. El dominio lo usa sin depender de ninguna librería.
public interface GameLogger {
    void info(String message);
    void warning(String message);
    void error(String message, Throwable cause);

    static GameLogger noop() {
        return new GameLogger() {
            public void info(String message) { }
            public void warning(String message) { }
            public void error(String message, Throwable cause) { }
        };
    }
}
