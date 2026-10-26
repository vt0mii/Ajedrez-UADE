package com.uade.ajedrez.infrastructure.adapter.in.http;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.GameView.MoveView;
import com.uade.ajedrez.application.dto.GameView.PieceView;
import com.uade.ajedrez.application.dto.GameView.PlayerView;

import java.util.List;

/**
 * Serializa el modelo de lectura {@code GameView} a JSON. Es la única clase que
 * conoce la forma exacta del contrato HTTP.
 */
final class GameViewJson {

    private GameViewJson() {
    }

    static String toJson(GameView game) {
        StringBuilder json = new StringBuilder("{");
        field(json, "id", Json.quote(game.id())).append(',');
        field(json, "status", Json.quote(game.status().name())).append(',');
        field(json, "turn", Json.quote(game.turn().name())).append(',');
        field(json, "turnNumber", String.valueOf(game.turnNumber())).append(',');
        field(json, "whitePlayer", player(game.whitePlayer())).append(',');
        field(json, "blackPlayer", player(game.blackPlayer())).append(',');
        field(json, "board", board(game.board())).append(',');
        field(json, "history", history(game.history())).append(',');
        field(json, "lastMove", game.lastMove() == null ? "null" : move(game.lastMove())).append(',');
        field(json, "winner", game.winner() == null ? "null" : Json.quote(game.winner().name()));
        return json.append('}').toString();
    }

    private static StringBuilder field(StringBuilder json, String name, String value) {
        return json.append('"').append(name).append("\":").append(value);
    }

    private static String player(PlayerView player) {
        return "{\"name\":" + Json.quote(player.name())
                + ",\"color\":" + Json.quote(player.color().name()) + "}";
    }

    private static String board(PieceView[][] board) {
        StringBuilder json = new StringBuilder("[");
        for (int row = 0; row < board.length; row++) {
            if (row > 0) {
                json.append(',');
            }
            json.append('[');
            for (int column = 0; column < board[row].length; column++) {
                if (column > 0) {
                    json.append(',');
                }
                json.append(piece(board[row][column]));
            }
            json.append(']');
        }
        return json.append(']').toString();
    }

    private static String piece(PieceView piece) {
        if (piece == null) {
            return "null";
        }
        return "{\"type\":" + Json.quote(piece.type().name())
                + ",\"color\":" + Json.quote(piece.color().name()) + "}";
    }

    private static String history(List<MoveView> history) {
        StringBuilder json = new StringBuilder("[");
        for (int index = 0; index < history.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append(move(history.get(index)));
        }
        return json.append(']').toString();
    }

    private static String move(MoveView move) {
        return "{\"from\":" + Json.quote(move.from()) + ",\"to\":" + Json.quote(move.to()) + "}";
    }
}
