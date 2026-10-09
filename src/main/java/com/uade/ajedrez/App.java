package com.uade.ajedrez;

import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.infrastructure.adapter.in.ConsoleView;
import com.uade.ajedrez.infrastructure.adapter.out.GameRepository;

public class App {
    public static void main(String[] args) {
        // Composition root
        Board board = new Board();
        MovementService movementService = new MovementService();

        Player whitePlayer = new Player("Tomi", Color.WHITE);
        Player blackPlayer = new Player("JuanDoe", Color.BLACK);

        ChessManager chessManager = new ChessManager(whitePlayer, blackPlayer, board, movementService);
        @SuppressWarnings("unused")
        GameRepository repository = new GameRepository();
        ConsoleView consoleView = new ConsoleView(chessManager);

        consoleView.start();
    }
}
