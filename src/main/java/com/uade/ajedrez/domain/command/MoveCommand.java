package com.uade.ajedrez.domain.command;

import com.uade.ajedrez.domain.factory.PieceFactory;
import com.uade.ajedrez.domain.model.Board;
import com.uade.ajedrez.domain.model.Move;
import com.uade.ajedrez.domain.model.Piece;
import com.uade.ajedrez.domain.model.PieceType;

// Movimiento que se puede deshacer. Antes de ejecutarlo, las reglas ya se
// validaron. Si hay varios sobre el mismo tablero, se deshacen al revés.
// El turno y el estado de la partida no son responsabilidad de esto.
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
        promoteIfNeeded();
        executed = true;
    }

    // El peon que llega a la ultima fila se convierte en la pieza elegida. Si
    // el movimiento no trae ninguna, se usa la reina.
    private void promoteIfNeeded() {
        if (movedPiece.getType() != PieceType.PAWN
                || !move.reachesLastRank(movedPiece.getColor())) {
            return;
        }
        board.getSquare(move.getDestination()).setPiece(
                PieceFactory.create(move.promotionOrQueen(), movedPiece.getColor()));
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
