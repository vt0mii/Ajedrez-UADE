package com.uade.ajedrez;

import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.infrastructure.adapter.in.ConsoleView;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.infrastructure.adapter.out.JavaGameLogger;
import com.uade.ajedrez.application.ChessGameService;
import com.uade.ajedrez.infrastructure.adapter.in.http.ChessHttpServer;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        GameLogger logger = new JavaGameLogger();
        try {
            if (args.length > 0 && args[0].equals("--web")) {
                int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;
                ChessHttpServer server = new ChessHttpServer(port, new ChessGameService(logger), logger);
                Runtime.getRuntime().addShutdownHook(new Thread(server::close));
                server.start();
                logger.info("Chess API running at http://127.0.0.1:" + server.getPort());
            } else {
                run(logger);
            }
        } catch (IOException | RuntimeException e) {
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
