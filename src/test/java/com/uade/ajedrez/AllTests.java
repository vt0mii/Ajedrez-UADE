package com.uade.ajedrez;

import com.uade.ajedrez.domain.command.MoveCommandTest;
import com.uade.ajedrez.domain.model.BoardTest;
import com.uade.ajedrez.domain.model.ChessManagerTest;
import com.uade.ajedrez.domain.service.MovementServiceTest;

public class AllTests {
    
    public static void main(String[] args) {
        System.out.println("Corriendo todos los tests");
        
        try {
            MoveCommandTest.main(args);
            System.out.println();

            BoardTest.main(args);
            System.out.println();
            
            MovementServiceTest.main(args);
            System.out.println();
            
            ChessManagerTest.main(args);
            System.out.println();
            
            System.out.println("All Tests Passed!");
        } catch (AssertionError e) {
            System.err.println("\nX TEST FAILED: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\nX TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
