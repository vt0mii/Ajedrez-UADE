package com.uade.ajedrez.domain;

import com.uade.ajedrez.domain.command.MoveCommand;
import com.uade.ajedrez.domain.factory.PieceFactory;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.MovementService;

public class PromotionTest {

    public static void main(String[] args) {
        System.out.println("Ejecutando tests de promocion...\n");

        testPromocionPorDefectoAReina();
        testPromocionElegida();
        testPromocionDeNegras();
        testRechazaPiezaInvalida();
        testRechazaPromocionFueraDeLaUltimaFila();
        testUndoVuelveAlPeon();

        System.out.println("\nTodos los tests de promocion pasaron!");
    }

    static void testPromocionPorDefectoAReina() {
        ChessGame game = whitePawnOnE7();

        Move move = new Move(new Position(1, 4), new Position(0, 4));
        assert game.makeMove(move).wasPossible() : "e7-e8 deberia ser valido";

        assert pieceAt(game, 0, 4).getType() == PieceType.QUEEN
                : "sin pieza elegida el peon deberia convertirse en reina";
        assert game.getHistory().get(0).getPromotion() == PieceType.QUEEN
                : "el historial deberia guardar la reina";

        System.out.println("V testPromocionPorDefectoAReina paso");
    }

    static void testPromocionElegida() {
        ChessGame game = whitePawnOnE7();

        Move move = new Move(new Position(1, 4), new Position(0, 4), PieceType.KNIGHT);
        assert game.makeMove(move).wasPossible() : "e7-e8 con caballo deberia ser valido";

        assert pieceAt(game, 0, 4).getType() == PieceType.KNIGHT
                : "el peon deberia convertirse en caballo";
        assert game.getHistory().get(0).getPromotion() == PieceType.KNIGHT
                : "el historial deberia guardar el caballo";

        System.out.println("V testPromocionElegida paso");
    }

    static void testPromocionDeNegras() {
        Board board = Board.empty();
        board.getSquare(new Position(6, 4)).setPiece(PieceFactory.create(PieceType.PAWN, Color.BLACK));
        board.getSquare(new Position(7, 0)).setPiece(PieceFactory.create(PieceType.KING, Color.WHITE));
        board.getSquare(new Position(0, 7)).setPiece(PieceFactory.create(PieceType.KING, Color.BLACK));

        ChessGame game = ChessGame.newGame(new GameId("g_promocion_negra"),
                new Player("Blanco", Color.WHITE), new Player("Negro", Color.BLACK),
                board, new MovementService());

        assert game.makeMove(new Move(new Position(7, 0), new Position(7, 1))).wasPossible()
                : "la espera de blancas deberia ser valida";

        Move move = new Move(new Position(6, 4), new Position(7, 4), PieceType.ROOK);
        assert game.makeMove(move).wasPossible() : "e2-e1 con torre deberia ser valido";

        Piece promoted = pieceAt(game, 7, 4);
        assert promoted.getType() == PieceType.ROOK : "el peon negro deberia ser torre";
        assert promoted.getColor() == Color.BLACK : "la torre deberia ser negra";

        System.out.println("V testPromocionDeNegras paso");
    }

    static void testRechazaPiezaInvalida() {
        ChessGame game = whitePawnOnE7();

        MoveResult result = game.makeMove(
                new Move(new Position(1, 4), new Position(0, 4), PieceType.KING));

        assert !result.wasPossible() : "un peon no puede ascender a rey";
        assert result.getReason() == Reason.NOT_AVAILABLE : "el motivo deberia ser NOT_AVAILABLE";
        assert pieceAt(game, 1, 4).getType() == PieceType.PAWN
                : "el peon deberia seguir en e7";

        System.out.println("V testRechazaPiezaInvalida paso");
    }

    static void testRechazaPromocionFueraDeLaUltimaFila() {
        MovementService service = new MovementService();
        Board board = new Board();

        Move move = new Move(new Position(6, 4), new Position(5, 4), PieceType.QUEEN);
        MoveResult result = service.validateMove(board, move, Color.WHITE);

        assert !result.wasPossible() : "no se puede elegir pieza fuera de la ultima fila";

        System.out.println("V testRechazaPromocionFueraDeLaUltimaFila paso");
    }

    static void testUndoVuelveAlPeon() {
        Board board = Board.empty();
        board.getSquare(new Position(1, 4)).setPiece(PieceFactory.create(PieceType.PAWN, Color.WHITE));
        board.getSquare(new Position(7, 0)).setPiece(PieceFactory.create(PieceType.KING, Color.WHITE));
        board.getSquare(new Position(0, 7)).setPiece(PieceFactory.create(PieceType.KING, Color.BLACK));

        MoveCommand command = new MoveCommand(board,
                new Move(new Position(1, 4), new Position(0, 4), PieceType.KNIGHT));
        command.execute();
        assert board.getPiece(new Position(0, 4)).getType() == PieceType.KNIGHT
                : "el peon deberia ascender";

        command.undo();
        assert board.getPiece(new Position(1, 4)).getType() == PieceType.PAWN
                : "al deshacer deberia volver el peon";
        assert board.getPiece(new Position(0, 4)) == null
                : "la ultima fila deberia quedar vacia";

        command.execute();
        assert board.getPiece(new Position(0, 4)).getType() == PieceType.KNIGHT
                : "al rehacer deberia volver a ascender";

        System.out.println("V testUndoVuelveAlPeon paso");
    }

    private static ChessGame whitePawnOnE7() {
        Board board = Board.empty();
        board.getSquare(new Position(1, 4)).setPiece(PieceFactory.create(PieceType.PAWN, Color.WHITE));
        board.getSquare(new Position(7, 0)).setPiece(PieceFactory.create(PieceType.KING, Color.WHITE));
        board.getSquare(new Position(0, 7)).setPiece(PieceFactory.create(PieceType.KING, Color.BLACK));

        return ChessGame.newGame(new GameId("g_promocion"),
                new Player("Blanco", Color.WHITE), new Player("Negro", Color.BLACK),
                board, new MovementService());
    }

    private static Piece pieceAt(ChessGame game, int row, int column) {
        return game.getBoard().getPiece(new Position(row, column));
    }
}
