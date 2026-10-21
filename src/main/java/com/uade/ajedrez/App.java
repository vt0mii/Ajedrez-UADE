package com.uade.ajedrez;

import com.uade.ajedrez.application.port.in.ChessGameUseCases;
import com.uade.ajedrez.application.port.out.GameRepositoryPort;
import com.uade.ajedrez.application.service.ChessGameService;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.infrastructure.adapter.in.console.ConsoleView;
import com.uade.ajedrez.infrastructure.adapter.out.JavaGameLogger;
import com.uade.ajedrez.infrastructure.adapter.out.persistence.FileGameRepository;

import java.nio.file.Path;

/**
 * Punto de composición: único lugar que conecta los adaptadores concretos con
 * la aplicación. Para agregar el adaptador web solo se modifica esta clase.
 */
public class App {
    public static void main(String[] args) {
        GameLogger logger = new JavaGameLogger();
        try {
            run(logger);
        } catch (RuntimeException e) {
            logger.error("Unexpected application failure", e);
            System.err.println("The application could not continue. See the log for details.");
            System.exit(1);
        }
    }

    private static void run(GameLogger logger) {
        MovementService movementService = new MovementService();
        GameRepositoryPort repository = new FileGameRepository(Path.of("games"), movementService);
        ChessGameUseCases useCases = new ChessGameService(repository, movementService, logger);

        new ConsoleView(useCases, logger).start();
    }
}
