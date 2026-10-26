package com.uade.ajedrez;

import com.uade.ajedrez.application.service.ChessGameServiceTest;
import com.uade.ajedrez.domain.command.MoveCommandTest;
import com.uade.ajedrez.domain.model.BoardTest;
import com.uade.ajedrez.domain.model.ChessGameTest;
import com.uade.ajedrez.domain.service.MovementServiceTest;
import com.uade.ajedrez.infrastructure.adapter.in.http.HttpGameServerTest;
import com.uade.ajedrez.infrastructure.adapter.out.persistence.GameSerializerTest;

public class AllTests {

    public static void main(String[] args) throws Exception {
        System.out.println("Corriendo todos los tests");

        try {
            BoardTest.main(args);
            System.out.println();

            MovementServiceTest.main(args);
            System.out.println();

            ChessGameTest.main(args);
            System.out.println();

            MoveCommandTest.main(args);
            System.out.println();

            ChessGameServiceTest.main(args);
            System.out.println();

            GameSerializerTest.main(args);
            System.out.println();

            HttpGameServerTest.main(args);
            System.out.println();

            ErrorHandlingTest.main(args);

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
