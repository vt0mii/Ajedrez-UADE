package com.uade.ajedrez;

import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.infrastructure.adapter.in.ConsoleView;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.infrastructure.adapter.out.JavaGameLogger;

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
        // Raiz de composicion
        Board board = new Board();
        MovementService movementService = new MovementService();

        Player whitePlayer = new Player("Tomi", Color.WHITE);
        Player blackPlayer = new Player("JuanDoe", Color.BLACK);

        ChessManager chessManager = new ChessManager(whitePlayer, blackPlayer, board, movementService, logger);
        ConsoleView consoleView = new ConsoleView(chessManager);

        consoleView.start();
    }
}
