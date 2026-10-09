package com.uade.ajedrez.domain.strategy;

import com.uade.ajedrez.domain.model.Board;
import com.uade.ajedrez.domain.model.Move;
import com.uade.ajedrez.domain.model.MoveResult;
import com.uade.ajedrez.domain.model.Piece;

public interface IMovementStrategy {
    MoveResult canMove(Board b, Move m, Piece p);
}
