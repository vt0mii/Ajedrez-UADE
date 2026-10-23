package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.port.in.Input;
import com.uade.ajedrez.domain.port.out.Output;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.domain.exception.InvalidMoveInputException;
import com.uade.ajedrez.domain.service.MovementService;

public class ChessManager {
    private final Board board;
    private final MovementService movementService;
    private final Turn turn;
    private State state;
    private final GameLogger logger;

    public ChessManager(Player whitePlayer, Player blackPlayer, Board board,
            MovementService movementService) {
        this(whitePlayer, blackPlayer, board, movementService, GameLogger.noop());
    }

    public ChessManager(Player whitePlayer, Player blackPlayer, Board board,
            MovementService movementService, GameLogger logger) {
        this.board = board;
        this.movementService = movementService;
        this.turn = new Turn(whitePlayer, blackPlayer);
        this.state = State.IN_PROGRESS;
        this.logger = java.util.Objects.requireNonNull(logger);
    }

    public MoveResult makeMove(Move move) {
        if (state == State.CHECKMATE) {
            return new MoveResult(false, Reason.NOT_AVAILABLE, "The game has already ended");
        }

        Color currentColor = turn.getCurrentPlayer().getColor();
        MoveResult validation = movementService.validateMove(board, move, currentColor);
        if (!validation.wasPossible()) {
            logger.warning("Move rejected: " + validation.getMessage());
            return validation;
        }

        board.movePiece(move);
        turn.nextTurn();

        updateState();
        logger.info("Move completed by " + currentColor + "; state=" + state);

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
        logger.info("Game started");
        while (state != State.CHECKMATE) {
            output.show("\n" + board);
            output.show("Turn #" + turn.getTurnNumber() + " - " + turn.getCurrentPlayer().getName() + " (" + turn.getCurrentPlayer().getColor() + ")");
            if (state == State.CHECK) {
                output.show("CHECK! The king is in danger.");
            }
            output.show("Enter move (e.g: e2 e4) or quit: ");

            String inputLine = input.readLine();
            if (inputLine == null) {
                logger.info("Game stopped: end of input");
                output.show("Input closed. Game stopped.");
                return;
            }
            inputLine = inputLine.trim();

            if (inputLine.equalsIgnoreCase("quit")) {
                logger.info("Game abandoned");
                output.show("Game abandoned.");
                return;
            }

            try {
                String[] parts = inputLine.split("\\s+");
                if (parts.length != 2) {
                    throw new InvalidMoveInputException("Invalid format. Use: origin destination (e.g: e2 e4)");
                }

                Position origin = Position.fromAlgebraic(parts[0]);
                Position destination = Position.fromAlgebraic(parts[1]);

                Move move = new Move(origin, destination);
                MoveResult result = makeMove(move);

                if (result.wasPossible()) {
                    output.show("Move made: " + parts[0] + " -> " + parts[1]);
                } else {
                    output.show("Invalid move: " + result.getMessage());
                }
            } catch (InvalidMoveInputException e) {
                logger.warning(e.getMessage());
                output.show("Error: " + e.getMessage());
            }
        }

        output.show("\n" + board);
        output.show("CHECKMATE! Game over.");
        logger.info("Game ended: checkmate");
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
