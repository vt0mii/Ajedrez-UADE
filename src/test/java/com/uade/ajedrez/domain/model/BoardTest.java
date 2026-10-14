package com.uade.ajedrez.domain.model;

import com.uade.ajedrez.domain.factory.PieceFactory;

public class BoardTest {
    
    public static void main(String[] args) {
        System.out.println("Ejecutando tests de Board...\n");
        
        testTableroInicial();
        testObtenerPieza();
        testMoverPieza();
        testMoverPiezaACasillaVacia();
        testCapturarPieza();
        
        System.out.println("\nTodos los tests de Board pasaron!");
    }
    
    static void testTableroInicial() {
        Board board = new Board();
        
        // Verificar esquinas
        Piece rook = board.getPiece(new Position(0, 0));
        assert rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == Color.BLACK 
            : "Se esperaba TORRE NEGRA en A8";
            
        Piece king = board.getPiece(new Position(0, 4));
        assert king != null && king.getType() == PieceType.KING && king.getColor() == Color.BLACK 
            : "Se esperaba REY NEGRO en E8";
            
        // Verificar casillas vacias
        assert board.getPiece(new Position(3, 3)) == null : "Se esperaba casilla vacia en D5";
        assert board.getPiece(new Position(4, 4)) == null : "Se esperaba casilla vacia en E4";
            
        System.out.println("V testTableroInicial paso");
    }
    
    static void testObtenerPieza() {
        Board board = new Board();
        
        Piece pawn = board.getPiece(new Position(1, 0));
        assert pawn != null && pawn.getType() == PieceType.PAWN && pawn.getColor() == Color.BLACK 
            : "Se esperaba PEoN NEGRO en A7";
            
        System.out.println("V testObtenerPieza paso");
    }
    
    static void testMoverPieza() {
        Board board = new Board();
        
        // Mover peon blanco de E2 a E4
        Move move = new Move(new Position(6, 4), new Position(4, 4));
        board.movePiece(move);
        
        assert board.getPiece(new Position(6, 4)) == null : "E2 deberia estar vacio";
        Piece pawn = board.getPiece(new Position(4, 4));
        assert pawn != null && pawn.getType() == PieceType.PAWN : "El peon deberia estar en E4";
        
        System.out.println("V testMoverPieza paso");
    }
    
    static void testMoverPiezaACasillaVacia() {
        Board board = new Board();
        
        // Mover caballo de G1 a F3
        Move move = new Move(new Position(7, 6), new Position(5, 5));
        board.movePiece(move);
        
        assert board.getPiece(new Position(7, 6)) == null : "G1 deberia estar vacio";
        Piece knight = board.getPiece(new Position(5, 5));
        assert knight != null && knight.getType() == PieceType.KNIGHT : "El caballo deberia estar en F3";
        
        System.out.println("V testMoverPiezaACasillaVacia paso");
    }
    
    static void testCapturarPieza() {
        Board board = new Board();
        
        // Colocar piezas manualmente para test de captura
        board.getSquare(new Position(3, 3)).setPiece(PieceFactory.create(PieceType.PAWN, Color.WHITE));
        board.getSquare(new Position(4, 4)).setPiece(PieceFactory.create(PieceType.PAWN, Color.BLACK));
        
        // Peon blanco captura peon negro
        Move move = new Move(new Position(3, 3), new Position(4, 4));
        board.movePiece(move);
        
        assert board.getPiece(new Position(3, 3)) == null : "D5 deberia estar vacio";
        Piece captured = board.getPiece(new Position(4, 4));
        assert captured != null && captured.getColor() == Color.WHITE : "El peon blanco deberia capturar en E4";
        
        System.out.println("V testCapturarPieza paso");
    }
}
