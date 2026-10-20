package com.uade.ajedrez.infrastructure.adapter.out;

import com.uade.ajedrez.domain.port.out.GameLogger;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Uses the JDK logging implementation; no external dependencies required. */
public class JavaGameLogger implements GameLogger {
    private final Logger logger = Logger.getLogger("com.uade.ajedrez");

    public void info(String message) { logger.info(message); }
    public void warning(String message) { logger.warning(message); }
    public void error(String message, Throwable cause) {
        logger.log(Level.SEVERE, message, cause);
    }
}
