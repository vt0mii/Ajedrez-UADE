package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.command.MoveCommand;
import com.uade.ajedrez.domain.service.MovementService;

import java.util.ArrayList;
import java.util.List;

// Reproduce el historial desde la posición inicial estándar, sin tocar la partida original.
public final class ChessReplay {
    private final GameId id;
    private final Player white;
    private final Player black;
    private final GameState result;
    private final List<Move> moves;
    private final List<MoveCommand> commands = new ArrayList<>();
    private final List<GameState> states = new ArrayList<>();
    private final Board board = new Board();
    private final MovementService movementService;
    private int position;

    ChessReplay(ChessGame source, MovementService movementService) {
        id = source.getId();
        white = source.getTurn().getPlayer(Color.WHITE);
        black = source.getTurn().getPlayer(Color.BLACK);
        result = source.getState();
        moves = List.copyOf(source.getHistory());
        this.movementService = movementService;
        states.add(GameState.IN_PROGRESS);
    }

    public boolean next() {
        if (position == moves.size()) {
            return false;
        }
        if (position == commands.size()) {
            Move move = moves.get(position);
            MoveResult validation = movementService.validateMove(board, move, currentColor());
            if (!validation.wasPossible()) {
                throw new IllegalStateException("Invalid replay history at move " + (position + 1));
            }
            commands.add(new MoveCommand(board, move));
        }
        commands.get(position).execute();
        position++;
        if (states.size() == position) {
            Color color = currentColor();
            states.add(movementService.isCheckmate(board, color) ? GameState.CHECKMATE
                    : movementService.isInCheck(board, color) ? GameState.CHECK : GameState.IN_PROGRESS);
        }
        return true;
    }

    public boolean previous() {
        if (position == 0) {
            return false;
        }
        commands.get(position - 1).undo();
        position--;
        return true;
    }

    // Devuelve una copia, así el consumidor no puede modificar el tablero del replay.
    public ChessGame snapshot() {
        return ChessGame.restore(id, board.copy(), movementService,
                Turn.restore(white, black, position % 2, position / 2 + 1),
                states.get(position), moves.subList(0, position));
    }

    private Color currentColor() {
        return position % 2 == 0 ? Color.WHITE : Color.BLACK;
    }

    public int getPosition() { return position; }
    public int getTotalMoves() { return moves.size(); }
    public GameState getResult() { return result; }
}
