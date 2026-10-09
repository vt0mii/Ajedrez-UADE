package com.uade.ajedrez.infrastructure.adapter.in;

import com.uade.ajedrez.domain.model.ChessManager;
import com.uade.ajedrez.domain.port.in.Input;
import com.uade.ajedrez.domain.port.out.Output;

import java.util.Scanner;

public class ConsoleView implements Input, Output {
    private final Scanner scanner = new Scanner(System.in);
    private final ChessManager chessManager;

    public ConsoleView(ChessManager chessManager) {
        this.chessManager = chessManager;
    }

    public void start() {
        chessManager.playGame(this, this);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public void show(String message) {
        System.out.println(message);
    }
}
