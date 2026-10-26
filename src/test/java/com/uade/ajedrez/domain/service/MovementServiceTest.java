package com.uade.ajedrez.domain.service;

import com.uade.ajedrez.domain.factory.PieceFactory;
import com.uade.ajedrez.domain.model.*;

public class MovementServiceTest {

    public static void main(String[] args) {
        System.out.println("Ejecutando tests de MovementService...\n");

        testPeon();
        testCaballo();
        testTorre();
        testAlfil();
        testReina();
        testRey();
        testNoMoverPiezaOponente();
        testNoCapturarRey();
        testEnJaque();
        testEnJaqueMate();
        testJaqueMateConPiezasBloqueadas();

        System.out.println("\nTodos los tests de MovementService pasaron!");
    }

    static void testPeon() {
        MovementService service = new MovementService();
        Board board = new Board();

        Move move = new Move(new Position(6, 4), new Position(4, 4));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "E2-E4 deberia ser valido";

        System.out.println("V testPeon paso");
    }

    static void testCaballo() {
        MovementService service = new MovementService();
        Board board = new Board();

        Move move = new Move(new Position(7, 6), new Position(5, 5));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "G1-F3 deberia ser valido";

        System.out.println("V testCaballo paso");
    }

    static void testTorre() {
        MovementService service = new MovementService();
        Board board = new Board();

        board.getSquare(new Position(6, 0)).setPiece(null);

        Move move = new Move(new Position(7, 0), new Position(5, 0));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "A1-A3 deberia ser valido";

        System.out.println("V testTorre paso");
    }

    static void testAlfil() {
        MovementService service = new MovementService();
        Board board = new Board();

        board.getSquare(new Position(6, 3)).setPiece(null);
        board.getSquare(new Position(6, 4)).setPiece(null);
        board.getSquare(new Position(6, 5)).setPiece(null);

        Move move = new Move(new Position(7, 5), new Position(5, 3));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "F1-D3 deberia ser valido";

        System.out.println("V testAlfil paso");
    }

    static void testReina() {
        MovementService service = new MovementService();
        Board board = new Board();

        board.getSquare(new Position(6, 3)).setPiece(null);

        Move move = new Move(new Position(7, 3), new Position(5, 3));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "D1-D3 deberia ser valido";

        System.out.println("V testReina paso");
    }

    static void testRey() {
        MovementService service = new MovementService();
        Board board = new Board();

        board.getSquare(new Position(6, 4)).setPiece(null);

        Move move = new Move(new Position(7, 4), new Position(6, 4));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "E1-E2 deberia ser valido";

        System.out.println("V testRey paso");
    }

    static void testNoMoverPiezaOponente() {
        MovementService service = new MovementService();
        Board board = new Board();

        Move move = new Move(new Position(1, 0), new Position(3, 0));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert !result.wasPossible() : "No se deberia poder mover pieza del oponente";

        System.out.println("V testNoMoverPiezaOponente paso");
    }

    static void testNoCapturarRey() {
        MovementService service = new MovementService();
        Board board = Board.empty();
        board.getSquare(Position.fromAlgebraic("a1")).setPiece(PieceFactory.create(PieceType.KING, Color.WHITE));
        board.getSquare(Position.fromAlgebraic("d1")).setPiece(PieceFactory.create(PieceType.QUEEN, Color.WHITE));
        board.getSquare(Position.fromAlgebraic("d8")).setPiece(PieceFactory.create(PieceType.KING, Color.BLACK));

        Move move = new Move(Position.fromAlgebraic("d1"), Position.fromAlgebraic("d8"));
        MoveResult result = service.validateMove(board, move, Color.WHITE);

        assert !result.wasPossible() : "No se puede capturar el rey";
        assert result.getMessage().contains("king") : "El mensaje deberia explicar el rechazo";

        System.out.println("V testNoCapturarRey paso");
    }

    static void testEnJaque() {
        MovementService service = new MovementService();
        Board board = new Board();

        assert !service.isInCheck(board, Color.WHITE) : "Blancas no deberian estar en jaque";
        assert !service.isInCheck(board, Color.BLACK) : "Negras no deberian estar en jaque";

        System.out.println("V testEnJaque paso");
    }

    static void testEnJaqueMate() {
        MovementService service = new MovementService();
        Board board = new Board();

        assert !service.isCheckmate(board, Color.WHITE) : "Blancas no deberian estar en jaque mate";
        assert !service.isCheckmate(board, Color.BLACK) : "Negras no deberian estar en jaque mate";

        System.out.println("V testEnJaqueMate paso");
    }

    // Mate del pasillo: los peones encierran al rey y el caballo tiene jugadas
    // pseudo-legales que no sacan el jaque. Antes esto se escapaba porque
    // hasValidMove contaba movimientos imposibles.
    static void testJaqueMateConPiezasBloqueadas() {
        MovementService service = new MovementService();
        Board board = Board.empty();
        board.getSquare(Position.fromAlgebraic("e1")).setPiece(PieceFactory.create(PieceType.KING, Color.WHITE));
        board.getSquare(Position.fromAlgebraic("a8")).setPiece(PieceFactory.create(PieceType.ROOK, Color.WHITE));
        board.getSquare(Position.fromAlgebraic("g8")).setPiece(PieceFactory.create(PieceType.KING, Color.BLACK));
        board.getSquare(Position.fromAlgebraic("f7")).setPiece(PieceFactory.create(PieceType.PAWN, Color.BLACK));
        board.getSquare(Position.fromAlgebraic("g7")).setPiece(PieceFactory.create(PieceType.PAWN, Color.BLACK));
        board.getSquare(Position.fromAlgebraic("h7")).setPiece(PieceFactory.create(PieceType.PAWN, Color.BLACK));
        board.getSquare(Position.fromAlgebraic("b4")).setPiece(PieceFactory.create(PieceType.KNIGHT, Color.BLACK));

        assert service.isInCheck(board, Color.BLACK) : "Negras deberian estar en jaque";
        assert service.isCheckmate(board, Color.BLACK) : "Negras deberian estar en jaque mate";

        System.out.println("V testJaqueMateConPiezasBloqueadas paso");
    }
}