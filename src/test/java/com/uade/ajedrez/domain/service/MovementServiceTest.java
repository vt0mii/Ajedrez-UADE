package com.uade.ajedrez.domain.service;

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
        testEnJaque();
        testEnJaqueMate();
        
        System.out.println("\nTodos los tests de MovementService pasaron!");
    }
    
    static void testPeon() {
        MovementService service = new MovementService();
        Board board = new Board();
        
        // Peon blanco avanza 2 casillas: E2-E4
        Move move = new Move(new Position(6, 4), new Position(4, 4));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "E2-E4 deberia ser valido";
        
        System.out.println("V testPeon paso");
    }
    
    static void testCaballo() {
        MovementService service = new MovementService();
        Board board = new Board();
        
        // Caballo se mueve de G1 a F3
        Move move = new Move(new Position(7, 6), new Position(5, 5));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "G1-F3 deberia ser valido";
        
        System.out.println("V testCaballo paso");
    }
    
    static void testTorre() {
        MovementService service = new MovementService();
        Board board = new Board();
        
        // Limpiar camino para la torre
        board.getSquare(new Position(6, 0)).setPiece(null);
        
        // Torre de A1 a A3
        Move move = new Move(new Position(7, 0), new Position(5, 0));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "A1-A3 deberia ser valido";
        
        System.out.println("V testTorre paso");
    }
    
    static void testAlfil() {
        MovementService service = new MovementService();
        Board board = new Board();
        
        // Limpiar camino para el alfil
        board.getSquare(new Position(6, 3)).setPiece(null);
        board.getSquare(new Position(6, 4)).setPiece(null);
        board.getSquare(new Position(6, 5)).setPiece(null);
        
        // Alfil de F1 a D3
        Move move = new Move(new Position(7, 5), new Position(5, 3));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "F1-D3 deberia ser valido";
        
        System.out.println("V testAlfil paso");
    }
    
    static void testReina() {
        MovementService service = new MovementService();
        Board board = new Board();
        
        // Limpiar camino para la reina
        board.getSquare(new Position(6, 3)).setPiece(null);
        
        // Reina de D1 a D3
        Move move = new Move(new Position(7, 3), new Position(5, 3));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "D1-D3 deberia ser valido";
        
        System.out.println("V testReina paso");
    }
    
    static void testRey() {
        MovementService service = new MovementService();
        Board board = new Board();
        
        // Limpiar camino para el rey
        board.getSquare(new Position(6, 4)).setPiece(null);
        
        // Rey de E1 a E2
        Move move = new Move(new Position(7, 4), new Position(6, 4));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert result.wasPossible() : "E1-E2 deberia ser valido";
        
        System.out.println("V testRey paso");
    }
    
    static void testNoMoverPiezaOponente() {
        MovementService service = new MovementService();
        Board board = new Board();
        
        // Blancas intentan mover peon negro de A7 a A5
        Move move = new Move(new Position(1, 0), new Position(3, 0));
        MoveResult result = service.validateMove(board, move, Color.WHITE);
        assert !result.wasPossible() : "No se deberia poder mover pieza del oponente";
        
        System.out.println("V testNoMoverPiezaOponente paso");
    }
    
    static void testEnJaque() {
        MovementService service = new MovementService();
        Board board = new Board();
        
        // Inicialmente no hay jaque
        assert !service.isInCheck(board, Color.WHITE) : "Blancas no deberian estar en jaque";
        assert !service.isInCheck(board, Color.BLACK) : "Negras no deberian estar en jaque";
        
        System.out.println("V testEnJaque paso");
    }
    
    static void testEnJaqueMate() {
        MovementService service = new MovementService();
        Board board = new Board();
        
        // Inicialmente no hay jaque mate
        assert !service.isCheckmate(board, Color.WHITE) : "Blancas no deberian estar en jaque mate";
        assert !service.isCheckmate(board, Color.BLACK) : "Negras no deberian estar en jaque mate";
        
        System.out.println("V testEnJaqueMate paso");
    }
}
