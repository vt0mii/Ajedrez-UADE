package com.uade.ajedrez.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    // Recupera el turno del archivo guardado.
    public static Turn restore(Player player1, Player player2, int currentIndex, int turnNumber) {
        if (currentIndex < 0 || currentIndex > 1) {
            throw new IllegalArgumentException("Current player index must be 0 or 1");
        }
        if (turnNumber < 1) {
            throw new IllegalArgumentException("Turn number must be at least 1");
        }
        Turn turn = new Turn(player1, player2);
        turn.currentIndex = currentIndex;
        turn.turnNumber = turnNumber;
        return turn;
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

    public int getCurrentIndex() {
        return currentIndex;
    }

    public Player getPlayer(Color color) {
        for (Player player : players) {
            if (player.getColor() == color) {
                return player;
            }
        }
        throw new IllegalArgumentException("There is no player with color " + color);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }
}
