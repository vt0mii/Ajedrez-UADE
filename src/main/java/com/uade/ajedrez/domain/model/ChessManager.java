package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.port.in.Input;
import com.uade.ajedrez.domain.port.out.Output;
import com.uade.ajedrez.domain.service.MovementService;

public class ChessManager {
    private final Board board;
    private final MovementService movementService;
    private final Turn turn;
    private State state;

    public ChessManager(Player whitePlayer, Player blackPlayer, Board board,
            MovementService movementService) {
        this.board = board;
        this.movementService = movementService;
        this.turn = new Turn(whitePlayer, blackPlayer);
        this.state = State.IN_PROGRESS;
    }

    public MoveResult makeMove(Move move) {
        if (state == State.CHECKMATE) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "The game has already ended");
        }

        Color currentColor = turn.getCurrentPlayer().getColor();
        MoveResult validation = movementService.validateMove(board, move, currentColor);
        if (!validation.wasPossible()) {
            return validation;
        }

        board.movePiece(move);
        turn.nextTurn();

        updateState();

        return validation;
    }

    private void updateState() {
        Color currentColor = turn.getCurrentPlayer().getColor();
        if (movementService.isCheckmate(board, currentColor)) {
            state = State.CHECKMATE;
        } else if (movementService.isInCheck(board, currentColor)) {
            state = State.CHECK;
        } else {
            state = State.IN_PROGRESS;
        }
    }

    public void playGame(Input input, Output output) {
        while (state != State.CHECKMATE) {
            output.show("\n" + board);
            output.show("Turn #" + turn.getTurnNumber() + " - " + turn.getCurrentPlayer().getName() + " (" + turn.getCurrentPlayer().getColor() + ")");
            if (state == State.CHECK) {
                output.show("CHECK! The king is in danger.");
            }
            output.show("Enter move (e.g: e2 e4) or quit: ");

            String inputLine = input.readLine().trim();

            if (inputLine.equalsIgnoreCase("quit")) {
                output.show("Game abandoned.");
                return;
            }

            try {
                String[] parts = inputLine.split(" ");
                if (parts.length != 2) {
                    output.show("Invalid format. Use: origin destination (e.g: e2 e4)");
                    continue;
                }

                Position origin = parseCoordinate(parts[0]);
                Position destination = parseCoordinate(parts[1]);

                Move move = new Move(origin, destination);
                MoveResult result = makeMove(move);

                if (result.wasPossible()) {
                    output.show("Move made: " + parts[0] + " -> " + parts[1]);
                } else {
                    output.show("Invalid move: " + result.getMessage());
                }
            } catch (IllegalArgumentException e) {
                output.show("Error: " + e.getMessage());
            }
        }

        output.show("\n" + board);
        output.show("CHECKMATE! Game over.");
    }

    private Position parseCoordinate(String coord) {
        if (coord.length() != 2) {
            throw new IllegalArgumentException("Invalid coordinate: " + coord);
        }
        String letters = "abcdefgh";
        String numbers = "87654321";

        int column = letters.indexOf(coord.charAt(0));
        int row = numbers.indexOf(coord.charAt(1));

        if (row < 0 || row >= 8 || column < 0 || column >= 8) {
            throw new IllegalArgumentException("Coordinate out of board: " + coord);
        }

        return new Position(row, column);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Color getCurrentTurn() {
        return turn.getCurrentPlayer().getColor();
    }

    public Player getCurrentPlayer() {
        return turn.getCurrentPlayer();
    }

    public Turn getTurn() {
        return turn;
    }

    public Board getBoard() {
        return board;
    }
}
