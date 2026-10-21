package com.uade.ajedrez.application.service;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.MoveCommand;
import com.uade.ajedrez.application.dto.NewGameCommand;
import com.uade.ajedrez.application.exception.GameNotFoundException;
import com.uade.ajedrez.application.exception.InvalidCommandException;
import com.uade.ajedrez.application.exception.MoveRejectedException;
import com.uade.ajedrez.domain.exception.InvalidMoveInputException;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.infrastructure.adapter.out.persistence.InMemoryGameRepository;

import java.util.List;

public class ChessGameServiceTest {

    public static void main(String[] args) {
        System.out.println("Ejecutando tests de ChessGameService...\n");

        testCrearPartida();
        testHacerMovimientoYPersistencia();
        testMovimientoIlegal();
        testPartidaInexistente();
        testIdInvalido();
        testCoordenadaInvalida();
        testMovimientosLegales();
        testAbandonarPartida();
        testNombresPorDefecto();

        System.out.println("\nTodos los tests de ChessGameService pasaron!");
    }

    static void testCrearPartida() {
        ChessGameService service = newService();
        GameView game = service.createGame(new NewGameCommand("Tomi", "Juan"));

        assert game.id().startsWith("g_") : "El id deberia empezar con g_";
        assert game.status() == GameState.IN_PROGRESS : "Estado inicial incorrecto";
        assert game.turn() == Color.WHITE : "Deberian empezar las blancas";
        assert game.turnNumber() == 1 : "Deberia ser el turno 1";
        assert game.whitePlayer().name().equals("Tomi") : "Nombre de blancas incorrecto";
        assert game.blackPlayer().name().equals("Juan") : "Nombre de negras incorrecto";
        assert game.board()[7][4].type() == PieceType.KING : "E1 deberia tener el rey blanco";
        assert game.board()[6][4].type() == PieceType.PAWN : "E2 deberia tener un peon blanco";
        assert game.history().isEmpty() : "No deberia haber movimientos";

        System.out.println("V testCrearPartida paso");
    }

    static void testHacerMovimientoYPersistencia() {
        ChessGameService service = newService();
        GameView created = service.createGame(new NewGameCommand("Tomi", "Juan"));

        GameView moved = service.makeMove(created.id(), new MoveCommand("e2", "e4"));

        assert moved.turn() == Color.BLACK : "Deberia jugar negras";
        assert moved.history().size() == 1 : "Deberia haber un movimiento";
        assert moved.lastMove().from().equals("e2") : "Origen incorrecto";
        assert moved.lastMove().to().equals("e4") : "Destino incorrecto";
        assert moved.board()[4][4] != null : "E4 deberia tener una pieza";
        assert moved.board()[6][4] == null : "E2 deberia estar vacio";

        GameView reloaded = service.getGame(created.id());
        assert reloaded.history().size() == 1 : "El movimiento deberia persistir";
        assert reloaded.turn() == Color.BLACK : "El turno deberia persistir";

        System.out.println("V testHacerMovimientoYPersistencia paso");
    }

    static void testMovimientoIlegal() {
        ChessGameService service = newService();
        GameView game = service.createGame(new NewGameCommand("Tomi", "Juan"));

        try {
            service.makeMove(game.id(), new MoveCommand("e2", "e5"));
            throw new AssertionError("E2-E5 deberia rechazarse");
        } catch (MoveRejectedException e) {
            assert e.getReason() == Reason.NOT_AVAILABLE : "La razon deberia ser NOT_AVAILABLE";
        }

        GameView unchanged = service.getGame(game.id());
        assert unchanged.turn() == Color.WHITE : "El turno no deberia cambiar";
        assert unchanged.history().isEmpty() : "No deberia registrarse el movimiento";

        System.out.println("V testMovimientoIlegal paso");
    }

    static void testPartidaInexistente() {
        ChessGameService service = newService();

        try {
            service.getGame("g_unknown");
            throw new AssertionError("Deberia lanzar GameNotFoundException");
        } catch (GameNotFoundException e) {
            assert e.getGameId().equals("g_unknown") : "El id deberia conservarse";
        }

        System.out.println("V testPartidaInexistente paso");
    }

    static void testIdInvalido() {
        ChessGameService service = newService();

        try {
            service.getGame("../../etc/passwd");
            throw new AssertionError("Deberia lanzar InvalidCommandException");
        } catch (InvalidCommandException e) {
            assert e.getMessage().contains("Invalid game id") : "Mensaje inesperado: " + e.getMessage();
        }

        System.out.println("V testIdInvalido paso");
    }

    static void testCoordenadaInvalida() {
        ChessGameService service = newService();
        GameView game = service.createGame(new NewGameCommand("Tomi", "Juan"));

        try {
            service.makeMove(game.id(), new MoveCommand("z9", "e4"));
            throw new AssertionError("La coordenada Z9 deberia fallar");
        } catch (InvalidMoveInputException e) {
            assert e.getMessage().contains("z9") : "El mensaje deberia mencionar la coordenada";
        }

        System.out.println("V testCoordenadaInvalida paso");
    }

    static void testMovimientosLegales() {
        ChessGameService service = newService();
        GameView game = service.createGame(new NewGameCommand("Tomi", "Juan"));

        List<String> fromE2 = service.legalMoves(game.id(), "e2");
        assert fromE2.contains("e3") && fromE2.contains("e4") : "E2 deberia permitir E3 y E4";
        assert service.legalMoves(game.id(), "e7").isEmpty() : "No se mueven piezas del oponente";

        System.out.println("V testMovimientosLegales paso");
    }

    static void testAbandonarPartida() {
        ChessGameService service = newService();
        GameView game = service.createGame(new NewGameCommand("Tomi", "Juan"));

        GameView abandoned = service.abandonGame(game.id());
        assert abandoned.status() == GameState.ABANDONED : "La partida deberia quedar abandonada";
        assert abandoned.status().isFinished() : "La partida deberia estar terminada";

        try {
            service.makeMove(game.id(), new MoveCommand("e2", "e4"));
            throw new AssertionError("No se deberia poder mover en una partida terminada");
        } catch (MoveRejectedException e) {
            assert e.getReason() == Reason.GAME_OVER : "La razon deberia ser GAME_OVER";
        }

        System.out.println("V testAbandonarPartida paso");
    }

    static void testNombresPorDefecto() {
        ChessGameService service = newService();
        GameView game = service.createGame(new NewGameCommand("  ", null));

        assert game.whitePlayer().name().equals("White") : "Blancas deberian usar el nombre por defecto";
        assert game.blackPlayer().name().equals("Black") : "Negras deberian usar el nombre por defecto";

        System.out.println("V testNombresPorDefecto paso");
    }

    private static ChessGameService newService() {
        return new ChessGameService(new InMemoryGameRepository(), new MovementService());
    }
}
