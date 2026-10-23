package com.uade.ajedrez.domain.port.in;

import com.uade.ajedrez.domain.model.GameSnapshot;
import com.uade.ajedrez.domain.model.Move;
import com.uade.ajedrez.domain.model.MoveResult;

/** Use cases available to incoming adapters. */
public interface ChessGame {
    GameSnapshot getGame();
    MoveOutcome move(Move move);
    GameSnapshot newGame();

    record MoveOutcome(MoveResult result, GameSnapshot game) { }
}
