package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.factory.PieceFactory;
import com.uade.ajedrez.domain.service.MovementService;

import java.util.List;

public class ChessGameTest {

    public static void main(String[] args) {
        System.out.println("Ejecutando tests de ChessGame...\n");

        testEstadoInicial();
        testHacerMovimiento();
        testMovimientoInvalido();
        testAlternanciaTurno();
        testHistorial();
        testMovimientosLegales();
        testJaqueMateActualizaEstado();

        System.out.println("\nTodos los tests de ChessGame pasaron!");
    }

    static void testEstadoInicial() {
        ChessGame game = newGame();

        assert game.getState() == GameState.IN_PROGRESS : "Estado inicial deberia ser IN_PROGRESS";
        assert game.getCurrentPlayer().getColor() == Color.WHITE : "Blancas deberian empezar";
        assert game.getTurn().getTurnNumber() == 1 : "Deberia ser el turno 1";
        assert game.getHistory().isEmpty() : "No deberia haber movimientos";
        assert !game.isFinished() : "La partida no deberia estar terminada";

        System.out.println("V testEstadoInicial paso");
    }

    static void testHacerMovimiento() {
        ChessGame game = newGame();

        MoveResult result = game.makeMove(move("e2", "e4"));

        assert result.wasPossible() : "E2-E4 deberia funcionar";
        assert game.getCurrentPlayer().getColor() == Color.BLACK : "El turno deberia alternar a negras";
        assert game.getState() == GameState.IN_PROGRESS : "La partida deberia continuar";
        assert game.getBoard().getPiece(Position.fromAlgebraic("e4")) != null : "El peon deberia estar en E4";
        assert game.getBoard().getPiece(Position.fromAlgebraic("e2")) == null : "E2 deberia estar vacio";

        System.out.println("V testHacerMovimiento paso");
    }

    static void testMovimientoInvalido() {
        ChessGame game = newGame();

        MoveResult result = game.makeMove(move("e2", "e5"));

        assert !result.wasPossible() : "E2-E5 deberia fallar";
        assert game.getCurrentPlayer().getColor() == Color.WHITE : "El turno no deberia cambiar";
        assert game.getBoard().getPiece(Position.fromAlgebraic("e2")) != null : "El peon deberia seguir en E2";

        System.out.println("V testMovimientoInvalido paso");
    }

    static void testAlternanciaTurno() {
        ChessGame game = newGame();

        game.makeMove(move("e2", "e4"));
        assert game.getCurrentPlayer().getColor() == Color.BLACK : "El turno deberia ser de negras";

        game.makeMove(move("e7", "e5"));
        assert game.getCurrentPlayer().getColor() == Color.WHITE : "El turno deberia ser de blancas otra vez";
        assert game.getTurn().getTurnNumber() == 2 : "Deberia ser el turno 2";

        System.out.println("V testAlternanciaTurno paso");
    }

    static void testHistorial() {
        ChessGame game = newGame();

        game.makeMove(move("e2", "e4"));
        game.makeMove(move("e7", "e5"));

        assert game.getHistory().size() == 2 : "Deberia haber 2 movimientos";
        Move lastMove = game.getHistory().get(1);
        assert lastMove.getOrigin().toAlgebraic().equals("e7") : "El ultimo origen deberia ser E7";
        assert lastMove.getDestination().toAlgebraic().equals("e5") : "El ultimo destino deberia ser E5";

        System.out.println("V testHistorial paso");
    }

    static void testMovimientosLegales() {
        ChessGame game = newGame();

        List<String> fromE2 = game.legalMovesFrom(Position.fromAlgebraic("e2"));
        assert fromE2.contains("e3") && fromE2.contains("e4") : "E2 deberia permitir E3 y E4";
        assert fromE2.size() == 2 : "El peon de E2 solo deberia tener 2 movimientos";

        assert game.legalMovesFrom(Position.fromAlgebraic("e7")).isEmpty()
                : "No se pueden mover piezas del oponente";

        System.out.println("V testMovimientosLegales paso");
    }

    static void testJaqueMateActualizaEstado() {
        Board board = Board.empty();
        board.getSquare(Position.fromAlgebraic("e1")).setPiece(PieceFactory.create(PieceType.KING, Color.WHITE));
        board.getSquare(Position.fromAlgebraic("a1")).setPiece(PieceFactory.create(PieceType.ROOK, Color.WHITE));
        board.getSquare(Position.fromAlgebraic("g8")).setPiece(PieceFactory.create(PieceType.KING, Color.BLACK));
        board.getSquare(Position.fromAlgebraic("f7")).setPiece(PieceFactory.create(PieceType.PAWN, Color.BLACK));
        board.getSquare(Position.fromAlgebraic("g7")).setPiece(PieceFactory.create(PieceType.PAWN, Color.BLACK));
        board.getSquare(Position.fromAlgebraic("h7")).setPiece(PieceFactory.create(PieceType.PAWN, Color.BLACK));

        MovementService service = new MovementService();
        ChessGame game = ChessGame.newGame(new GameId("mate-test"),
                new Player("Blanco", Color.WHITE), new Player("Negro", Color.BLACK), board, service);

        MoveResult result = game.makeMove(move("a1", "a8"));

        assert result.wasPossible() : "A1-A8 deberia ser legal";
        assert game.getState() == GameState.CHECKMATE : "Deberia ser jaque mate";
        assert game.getCurrentPlayer().getColor() == Color.BLACK : "Negras son las que estan en mate";
        assert game.isFinished() : "La partida deberia estar terminada";

        System.out.println("V testJaqueMateActualizaEstado paso");
    }

    private static ChessGame newGame() {
        return ChessGame.newGame(
                new GameId("test-game"),
                new Player("Blanco", Color.WHITE),
                new Player("Negro", Color.BLACK),
                new Board(),
                new MovementService());
    }

    private static Move move(String from, String to) {
        return new Move(Position.fromAlgebraic(from), Position.fromAlgebraic(to));
    }
}