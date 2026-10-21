package com.uade.ajedrez.infrastructure.adapter.in.console;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.MoveCommand;
import com.uade.ajedrez.application.dto.NewGameCommand;
import com.uade.ajedrez.application.exception.GameNotFoundException;
import com.uade.ajedrez.application.exception.InvalidCommandException;
import com.uade.ajedrez.application.exception.MoveRejectedException;
import com.uade.ajedrez.application.port.in.ChessGameUseCases;
import com.uade.ajedrez.domain.exception.InvalidMoveInputException;
import com.uade.ajedrez.domain.port.out.GameLogger;

import java.util.List;
import java.util.Scanner;

/**
 * Adaptador de entrada. Traduce el texto de la consola a llamadas de casos de
 * uso y muestra los modelos de lectura resultantes. El bucle del juego vive
 * acá, no en el dominio.
 */
public class ConsoleView {

    private final ChessGameUseCases useCases;
    private final ConsoleRenderer renderer;
    private final Scanner scanner;
    private final GameLogger logger;

    public ConsoleView(ChessGameUseCases useCases) {
        this(useCases, new ConsoleRenderer(), new Scanner(System.in), GameLogger.noop());
    }

    public ConsoleView(ChessGameUseCases useCases, GameLogger logger) {
        this(useCases, new ConsoleRenderer(), new Scanner(System.in), logger);
    }

    public ConsoleView(ChessGameUseCases useCases, ConsoleRenderer renderer, Scanner scanner) {
        this(useCases, renderer, scanner, GameLogger.noop());
    }

    public ConsoleView(ChessGameUseCases useCases, ConsoleRenderer renderer, Scanner scanner,
            GameLogger logger) {
        this.useCases = useCases;
        this.renderer = renderer;
        this.scanner = scanner;
        this.logger = logger;
    }

    public void start() {
        String whiteName = ask("White player name (Enter for 'White'): ", "White");
        if (whiteName == null) {
            stopOnInputClosed();
            return;
        }
        String blackName = ask("Black player name (Enter for 'Black'): ", "Black");
        if (blackName == null) {
            stopOnInputClosed();
            return;
        }

        GameView game;
        try {
            game = useCases.createGame(new NewGameCommand(whiteName, blackName));
        } catch (InvalidCommandException e) {
            logger.warning(e.getMessage());
            renderer.show("Could not create the game: " + e.getMessage());
            return;
        }
        logger.info("Game started: " + game.id());

        while (true) {
            renderer.render(game);
            if (game.status().isFinished()) {
                logger.info("Game ended: " + game.status());
                return;
            }

            renderer.show("Enter move (e.g: e2 e4), 'legal e2' or 'quit': ");
            String line = readLine();
            if (line == null) {
                stopOnInputClosed();
                return;
            }
            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("quit")) {
                game = abandon(game);
                continue;
            }
            if (line.equalsIgnoreCase("help")) {
                renderer.show("Commands: 'e2 e4' to move, 'legal e2' to list moves, 'quit' to abandon.");
                continue;
            }
            if (line.toLowerCase().startsWith("legal ")) {
                showLegalMoves(game.id(), line.substring("legal ".length()).trim());
                continue;
            }

            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                String message = "Invalid format. Use: origin destination (e.g: e2 e4)";
                logger.warning(message);
                renderer.show(message);
                continue;
            }

            try {
                game = useCases.makeMove(game.id(), new MoveCommand(parts[0], parts[1]));
            } catch (MoveRejectedException e) {
                renderer.show("Invalid move: " + e.getMessage());
            } catch (InvalidMoveInputException | InvalidCommandException e) {
                logger.warning(e.getMessage());
                renderer.show("Error: " + e.getMessage());
            } catch (GameNotFoundException e) {
                renderer.show("Error: " + e.getMessage());
                return;
            }
        }
    }

    private GameView abandon(GameView current) {
        try {
            GameView abandoned = useCases.abandonGame(current.id());
            logger.info("Game abandoned by user");
            return abandoned;
        } catch (RuntimeException e) {
            logger.warning(e.getMessage());
            renderer.show("Could not abandon the game: " + e.getMessage());
            return current;
        }
    }

    private void showLegalMoves(String gameId, String from) {
        try {
            List<String> destinations = useCases.legalMoves(gameId, from);
            if (destinations.isEmpty()) {
                renderer.show("No legal moves from " + from + ".");
            } else {
                renderer.show("Legal moves from " + from + ": " + String.join(", ", destinations));
            }
        } catch (InvalidMoveInputException | InvalidCommandException | GameNotFoundException e) {
            logger.warning(e.getMessage());
            renderer.show("Error: " + e.getMessage());
        }
    }

    private String ask(String prompt, String defaultValue) {
        renderer.show(prompt);
        String answer = readLine();
        if (answer == null) {
            return null;
        }
        answer = answer.trim();
        return answer.isEmpty() ? defaultValue : answer;
    }

    /**
     * @return la siguiente línea, o null si la entrada se cerró (EOF).
     */
    private String readLine() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    private void stopOnInputClosed() {
        logger.info("Game stopped: end of input");
        renderer.show("Input closed. Game stopped.");
    }
}
