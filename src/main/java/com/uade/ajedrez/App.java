package com.uade.ajedrez;

import com.uade.ajedrez.application.port.in.ChessGameUseCases;
import com.uade.ajedrez.application.port.out.GameRepositoryPort;
import com.uade.ajedrez.application.service.ChessGameService;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.infrastructure.adapter.in.console.ConsoleView;
import com.uade.ajedrez.infrastructure.adapter.in.http.HttpGameServer;
import com.uade.ajedrez.infrastructure.adapter.out.JavaGameLogger;
import com.uade.ajedrez.infrastructure.adapter.out.persistence.FileGameRepository;

import java.io.IOException;
import java.nio.file.Path;

// Arranca la app. Es el único lugar donde se eligen los adaptadores concretos.
// Por defecto abre la consola; con --web levanta el servidor HTTP.
//
//   java -cp bin com.uade.ajedrez.App
//   java -cp bin com.uade.ajedrez.App --web
//   java -cp bin com.uade.ajedrez.App --web --port 9090
public class App {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        GameLogger logger = new JavaGameLogger();
        try {
            if (hasFlag(args, "--web")) {
                runWeb(logger, port(args));
            } else {
                runConsole(logger);
            }
        } catch (IOException e) {
            logger.error("The HTTP server could not start", e);
            System.err.println("The application could not continue. See the log for details.");
            System.exit(1);
        } catch (RuntimeException e) {
            logger.error("Unexpected application failure", e);
            System.err.println("The application could not continue. See the log for details.");
            System.exit(1);
        }
    }

    private static void runConsole(GameLogger logger) {
        new ConsoleView(buildUseCases(logger), logger).start();
    }

    private static void runWeb(GameLogger logger, int port) throws IOException {
        new HttpGameServer(buildUseCases(logger), logger, port).start();
    }

    private static ChessGameUseCases buildUseCases(GameLogger logger) {
        MovementService movementService = new MovementService();
        GameRepositoryPort repository = new FileGameRepository(Path.of("games"), movementService);
        return new ChessGameService(repository, movementService, logger);
    }

    private static boolean hasFlag(String[] args, String flag) {
        for (String arg : args) {
            if (arg.equals(flag)) {
                return true;
            }
        }
        return false;
    }

    private static int port(String[] args) {
        for (int index = 0; index + 1 < args.length; index++) {
            if (args[index].equals("--port")) {
                return Integer.parseInt(args[index + 1]);
            }
        }

        String fromEnvironment = System.getenv("PORT");
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return Integer.parseInt(fromEnvironment);
        }
        return DEFAULT_PORT;
    }
}
