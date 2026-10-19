package com.uade.ajedrez.domain.command;

import com.uade.ajedrez.domain.model.Board;
import com.uade.ajedrez.domain.model.Move;
import com.uade.ajedrez.domain.model.Piece;
import com.uade.ajedrez.domain.model.Position;

public class MoveCommandTest {
    public static void main(String[] args) {
        System.out.println("Ejecutando tests de MoveCommand...");
        testMoveAndUndo();
        testCaptureAndRepeatedReplay();
        testSequence();
        testInvalidLifecycle();
        System.out.println("Todos los tests de MoveCommand pasaron!");
    }

    static void testMoveAndUndo() {
        Board board = new Board();
        Piece[][] initial = snapshot(board);
        Position origin = new Position(6, 4);
        Position destination = new Position(4, 4);
        Piece pawn = board.getPiece(origin);
        ICommand command = new MoveCommand(board, new Move(origin, destination));

        assertBoard(board, initial);
        command.execute();
        assert board.getPiece(origin) == null : "El origen debe quedar vacio";
        assert board.getPiece(destination) == pawn : "Debe moverse la misma pieza";
        command.undo();
        assertBoard(board, initial);
    }

    static void testCaptureAndRepeatedReplay() {
        Board board = new Board();
        board.movePiece(new Move(new Position(6, 4), new Position(4, 4)));
        board.movePiece(new Move(new Position(1, 3), new Position(3, 3)));
        Piece[][] initial = snapshot(board);
        Position origin = new Position(4, 4);
        Position destination = new Position(3, 3);
        Piece pawn = board.getPiece(origin);
        ICommand command = new MoveCommand(board, new Move(origin, destination));

        for (int cycle = 0; cycle < 3; cycle++) {
            command.execute();
            assert board.getPiece(origin) == null : "El origen debe quedar vacio";
            assert board.getPiece(destination) == pawn : "La captura debe repetirse";
            command.undo();
            assertBoard(board, initial);
        }
    }

    static void testSequence() {
        Board board = new Board();
        Piece[][] initial = snapshot(board);
        ICommand[] commands = {
            new MoveCommand(board, new Move(new Position(6, 4), new Position(4, 4))),
            new MoveCommand(board, new Move(new Position(1, 3), new Position(3, 3))),
            new MoveCommand(board, new Move(new Position(4, 4), new Position(3, 3)))
        };
        for (ICommand command : commands) {
            command.execute();
        }
        Piece[][] end = snapshot(board);
        for (int index = commands.length - 1; index >= 0; index--) {
            commands[index].undo();
        }
        assertBoard(board, initial);
        for (ICommand command : commands) {
            command.execute();
        }
        assertBoard(board, end);
    }

    static void testInvalidLifecycle() {
        Board board = new Board();
        Piece[][] initial = snapshot(board);
        ICommand command = new MoveCommand(board,
                new Move(new Position(6, 4), new Position(4, 4)));
        expectException(IllegalStateException.class, command::undo);
        assertBoard(board, initial);
        command.execute();
        Piece[][] executed = snapshot(board);
        expectException(IllegalStateException.class, command::execute);
        assertBoard(board, executed);
        command.undo();
        expectException(IllegalStateException.class, command::undo);
        assertBoard(board, initial);
    }

    private static Piece[][] snapshot(Board board) {
        Piece[][] pieces = new Piece[Board.SIZE][Board.SIZE];
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                pieces[row][column] = board.getPiece(new Position(row, column));
            }
        }
        return pieces;
    }

    private static void assertBoard(Board board, Piece[][] expected) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                assert board.getPiece(new Position(row, column)) == expected[row][column]
                        : "Pieza inesperada en " + row + "," + column;
            }
        }
    }

    private static void expectException(Class<? extends RuntimeException> type, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            assert type.isInstance(exception) : "Excepcion inesperada: " + exception;
            return;
        }
        throw new AssertionError("Se esperaba " + type.getSimpleName());
    }
}
