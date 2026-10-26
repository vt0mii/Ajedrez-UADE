package com.uade.ajedrez.infrastructure.adapter.out;

import com.uade.ajedrez.domain.port.out.GameLogger;
import java.util.logging.Level;
import java.util.logging.Logger;

// Logger con java.util.logging. No necesita nada extra.
public class JavaGameLogger implements GameLogger {
    private final Logger logger = Logger.getLogger("com.uade.ajedrez");

    public void info(String message) { logger.info(message); }
    public void warning(String message) { logger.warning(message); }
    public void error(String message, Throwable cause) {
        logger.log(Level.SEVERE, message, cause);
    }
}
