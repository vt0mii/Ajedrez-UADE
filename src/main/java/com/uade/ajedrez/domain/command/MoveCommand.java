package com.uade.ajedrez.domain.command;

import com.uade.ajedrez.domain.model.Board;
import com.uade.ajedrez.domain.model.Move;
import com.uade.ajedrez.domain.model.Piece;

/**
 * Reversible board movement. Chess rules must be validated before execution.
 * Commands on the same board must be undone in reverse execution order.
 * Turn and game state are outside this command's responsibility.
 */
public class MoveCommand implements ICommand {
    private final Board board;
    private final Move move;
    private Piece movedPiece;
    private Piece capturedPiece;
    private boolean executed;

    public MoveCommand(Board board, Move move) {
        this.board = board;
        this.move = move;
    }

    @Override
    public void execute() {
        if (executed) {
            throw new IllegalStateException("Command has already been executed");
        }
        if (movedPiece == null) {
            movedPiece = board.getPiece(move.getOrigin());
            capturedPiece = board.getPiece(move.getDestination());
        }
        board.movePiece(move);
        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) {
            throw new IllegalStateException("Command has not been executed or is already undone");
        }
        board.getSquare(move.getOrigin()).setPiece(movedPiece);
        board.getSquare(move.getDestination()).setPiece(capturedPiece);
        executed = false;
    }
}
