package com.uade.ajedrez.application.dto;

import com.uade.ajedrez.domain.model.GameState;

public record ReplayView(   GameView game, 
                            int position,
                            int totalMoves, 
                            GameState result) {
}
