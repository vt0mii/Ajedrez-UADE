package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.service.MovementService;

public class ChessManagerTest {
    
    public static void main(String[] args) {
        System.out.println("Ejecutando tests de ChessManager...\n");
        
        testEstadoInicial();
        testHacerMovimiento();
        testMovimientoInvalido();
        testAlternanciaTurno();
        
        System.out.println("\nTodos los tests de ChessManager pasaron!");
    }
    
    static void testEstadoInicial() {
        MovementService service = new MovementService();
        Player white = new Player("Blanco", Color.WHITE);
        Player black = new Player("Negro", Color.BLACK);
        Board board = new Board();
        ChessManager manager = new ChessManager(white, black, board, service);
        
        assert manager.getState() == State.IN_PROGRESS : "Estado inicial deberia ser IN_PROGRESS";
        assert manager.getCurrentPlayer().getColor() == Color.WHITE : "Blancas deberian empezar";
        
        System.out.println("V testEstadoInicial paso");
    }
    
    static void testHacerMovimiento() {
        MovementService service = new MovementService();
        Player white = new Player("Blanco", Color.WHITE);
        Player black = new Player("Negro", Color.BLACK);
        Board board = new Board();
        ChessManager manager = new ChessManager(white, black, board, service);
        
        // Hacer movimiento valido: E2-E4
        Move move = new Move(new Position(6, 4), new Position(4, 4));
        MoveResult result = manager.makeMove(move);
        
        assert result.wasPossible() : "E2-E4 deberia funcionar";
        assert manager.getCurrentPlayer().getColor() == Color.BLACK : "El turno deberia alternar a negras";
        assert manager.getState() == State.IN_PROGRESS : "La partida deberia continuar";
        
        System.out.println("V testHacerMovimiento paso");
    }
    
    static void testMovimientoInvalido() {
        MovementService service = new MovementService();
        Player white = new Player("Blanco", Color.WHITE);
        Player black = new Player("Negro", Color.BLACK);
        Board board = new Board();
        ChessManager manager = new ChessManager(white, black, board, service);
        
        // Movimiento invalido: peon no puede avanzar 3 casillas (E2-E5)
        Move move = new Move(new Position(6, 4), new Position(3, 4));
        MoveResult result = manager.makeMove(move);
        
        assert !result.wasPossible() : "E2-E5 deberia fallar";
        assert manager.getCurrentPlayer().getColor() == Color.WHITE : "El turno no deberia cambiar con movimiento invalido";
        
        System.out.println("V testMovimientoInvalido paso");
    }
    
    static void testAlternanciaTurno() {
        MovementService service = new MovementService();
        Player white = new Player("Blanco", Color.WHITE);
        Player black = new Player("Negro", Color.BLACK);
        Board board = new Board();
        ChessManager manager = new ChessManager(white, black, board, service);
        
        // Blancas mueven E2-E4
        Move move1 = new Move(new Position(6, 4), new Position(4, 4));
        manager.makeMove(move1);
        assert manager.getCurrentPlayer().getColor() == Color.BLACK : "El turno deberia ser de negras";
        
        // Negras mueven E7-E5
        Move move2 = new Move(new Position(1, 4), new Position(3, 4));
        manager.makeMove(move2);
        assert manager.getCurrentPlayer().getColor() == Color.WHITE : "El turno deberia ser de blancas otra vez";
        
        System.out.println("V testAlternanciaTurno paso");
    }
}
