package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.domain.command.MoveCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agregado raíz de la partida. Contiene solo reglas: nada de consola, archivos
 * ni HTTP. Los adaptadores acceden a través de la capa de aplicación.
 */
public class ChessGame {
    private final GameId id;
    private final Board board;
    private final MovementService movementService;
    private final Turn turn;
    private final List<Move> history;
    private GameState state;

    private ChessGame(GameId id, Board board, MovementService movementService, Turn turn,
            GameState state, List<Move> history) {
        this.id = id;
        this.board = board;
        this.movementService = movementService;
        this.turn = turn;
        this.state = state;
        this.history = new ArrayList<>(history);
    }

    public static ChessGame newGame(GameId id, Player whitePlayer, Player blackPlayer,
            Board board, MovementService movementService) {
        if (id == null || board == null || movementService == null) {
            throw new IllegalArgumentException("Game id, board and movement service are required");
        }
        if (whitePlayer == null || whitePlayer.getColor() != Color.WHITE) {
            throw new IllegalArgumentException("White player must have color WHITE");
        }
        if (blackPlayer == null || blackPlayer.getColor() != Color.BLACK) {
            throw new IllegalArgumentException("Black player must have color BLACK");
        }
        return new ChessGame(id, board, movementService, new Turn(whitePlayer, blackPlayer),
                GameState.IN_PROGRESS, Collections.emptyList());
    }

    /**
     * Reconstruye una partida a partir de su estado persistido.
     */
    public static ChessGame restore(GameId id, Board board, MovementService movementService,
            Turn turn, GameState state, List<Move> history) {
        if (id == null || board == null || movementService == null || turn == null || state == null) {
            throw new IllegalArgumentException("Incomplete game state");
        }
        return new ChessGame(id, board, movementService, turn, state, history);
    }

    public MoveResult makeMove(Move move) {
        if (state.isFinished()) {
            return new MoveResult(false, Reason.GAME_OVER, "The game has already ended");
        }

        Color currentColor = getCurrentTurn();
        MoveResult validation = movementService.validateMove(board, move, currentColor);
        if (!validation.wasPossible()) {
            return validation;
        }

        new MoveCommand(board, move).execute();
        history.add(move);
        turn.nextTurn();
        updateState();

        return validation;
    }

    public void abandon() {
        if (!state.isFinished()) {
            state = GameState.ABANDONED;
        }
    }

    public ChessReplay startReplay() {
        if (!state.isFinished()) {
            throw new IllegalStateException("Replay requires a finished game");
        }
        return new ChessReplay(this, movementService);
    }

    /**
     * Destinos legales desde una casilla para el jugador actual.
     * Útil para el frontend sin reimplementar reglas.
     */
    public List<String> legalMovesFrom(Position origin) {
        if (state.isFinished() || !board.getSquare(origin).hasPiece()) {
            return Collections.emptyList();
        }
        Piece piece = board.getPiece(origin);
        Color currentColor = getCurrentTurn();
        if (piece.getColor() != currentColor) {
            return Collections.emptyList();
        }

        List<String> destinations = new ArrayList<>();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Position destination = new Position(row, column);
                Move move = new Move(origin, destination);
                if (movementService.validateMove(board, move, currentColor).wasPossible()) {
                    destinations.add(destination.toAlgebraic());
                }
            }
        }
        return destinations;
    }

    private void updateState() {
        Color currentColor = getCurrentTurn();
        if (movementService.isCheckmate(board, currentColor)) {
            state = GameState.CHECKMATE;
        } else if (movementService.isInCheck(board, currentColor)) {
            state = GameState.CHECK;
        } else {
            state = GameState.IN_PROGRESS;
        }
    }

    public GameId getId() {
        return id;
    }

    public GameState getState() {
        return state;
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

    public List<Move> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public boolean isFinished() {
        return state.isFinished();
    }
}
