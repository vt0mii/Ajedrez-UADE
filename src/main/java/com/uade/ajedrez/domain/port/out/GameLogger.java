package com.uade.ajedrez.domain.port.out;

/** Puerto de logging: el dominio no depende de un framework de logs. */
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
