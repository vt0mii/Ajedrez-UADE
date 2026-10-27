package com.uade.ajedrez.infrastructure.adapter.in.console;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.GameView.MoveView;
import com.uade.ajedrez.application.dto.GameView.PieceView;
import com.uade.ajedrez.domain.model.Board;
import com.uade.ajedrez.domain.model.Color;

import java.io.PrintStream;

// Solo dibuja el GameView. No decide nada del juego.
public class ConsoleRenderer {

    private final PrintStream out;

    public ConsoleRenderer() {
        this(System.out);
    }

    public ConsoleRenderer(PrintStream out) {
        this.out = out;
    }

    public void render(GameView game) {
        out.println();
        out.println(header(game));
        if (game.status() == com.uade.ajedrez.domain.model.GameState.CHECK) {
            out.println("CHECK! The king is in danger.");
        }
        out.println(renderBoard(game));
        if (game.lastMove() != null) {
            MoveView last = game.lastMove();
            out.println("Last move: " + last.from() + " -> " + last.to());
        }
        printResult(game);
    }

    public void show(String message) {
        out.println(message);
    }

    private String header(GameView game) {
        String currentName = game.turn() == Color.WHITE
                ? game.whitePlayer().name()
                : game.blackPlayer().name();
        return "Turn #" + game.turnNumber() + " - " + currentName + " (" + game.turn() + ")";
    }

    private void printResult(GameView game) {
        switch (game.status()) {
            case CHECKMATE:
                String winnerName = game.winner() == Color.WHITE
                        ? game.whitePlayer().name()
                        : game.blackPlayer().name();
                out.println("CHECKMATE! Winner: " + winnerName + " (" + game.winner() + ")");
                break;
            case ABANDONED:
                out.println("Game abandoned.");
                break;
            default:
                break;
        }
    }

    private String renderBoard(GameView game) {
        StringBuilder board = new StringBuilder();
        board.append("  a b c d e f g h\n");
        PieceView[][] pieces = game.board();
        for (int row = 0; row < Board.SIZE; row++) {
            board.append(Board.SIZE - row).append(' ');
            for (int column = 0; column < Board.SIZE; column++) {
                PieceView piece = pieces[row][column];
                board.append(piece == null ? '.' : symbol(piece));
                if (column < Board.SIZE - 1) {
                    board.append(' ');
                }
            }
            board.append(' ').append(Board.SIZE - row).append('\n');
        }
        board.append("  a b c d e f g h");
        return board.toString();
    }

    private char symbol(PieceView piece) {
        char symbol = switch (piece.type()) {
            case PAWN -> 'P';
            case KNIGHT -> 'N';
            case BISHOP -> 'B';
            case ROOK -> 'R';
            case QUEEN -> 'Q';
            case KING -> 'K';
        };
        return piece.color() == Color.BLACK ? Character.toLowerCase(symbol) : symbol;
    }
}
