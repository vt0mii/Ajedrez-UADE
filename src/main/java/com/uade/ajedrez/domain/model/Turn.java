package com.uade.ajedrez.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Turn {
    private final List<Player> players;
    private int currentIndex;
    private int turnNumber;

    public Turn(Player player1, Player player2) {
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("A player is not valid");
        }
        this.players = new ArrayList<>();
        Collections.addAll(this.players, player1, player2);
        this.currentIndex = 0;
        this.turnNumber = 1;
    }

    public Player getCurrentPlayer() {
        return players.get(currentIndex);
    }

    public boolean isTurnOf(Player player) {
        return getCurrentPlayer().equals(player);
    }

    public Player nextTurn() {
        currentIndex = 1 - currentIndex;
        if (currentIndex == 0) {
            turnNumber++;
        }
        return getCurrentPlayer();
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public List<Player> getPlayers() {
        return this.players;
    }
}
