package com.uade.ajedrez.application.service;

import com.uade.ajedrez.application.dto.ReplayView;
import com.uade.ajedrez.domain.model.ChessReplay;

/** Sesion transitoria sin acceso al repositorio. */
public final class ReplaySession {
    private final ChessReplay replay;

    public ReplaySession(ChessReplay replay) {
        this.replay = replay;
    }

    public ReplayView current() {
        return new ReplayView(GameViewMapper.toView(replay.snapshot()), replay.getPosition(),
                replay.getTotalMoves(), replay.getResult());
    }

    public ReplayView next() {
        replay.next();
        return current();
    }

    public ReplayView previous() {
        replay.previous();
        return current();
    }
}
