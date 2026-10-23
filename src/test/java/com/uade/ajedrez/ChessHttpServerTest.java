package com.uade.ajedrez;

import com.uade.ajedrez.application.ChessGameService;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.infrastructure.adapter.in.http.ChessHttpServer;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

/** Exercises the real HTTP adapter against the real application and domain. */
public class ChessHttpServerTest {
    public static void main(String[] args) throws Exception {
        ChessGameService game = new ChessGameService(GameLogger.noop());
        GameSnapshot original = game.getGame();
        try (ChessHttpServer server = new ChessHttpServer(0, game, GameLogger.noop())) {
            server.start();
            String base = "http://127.0.0.1:" + server.getPort();
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
            var initial = send(client, base, "GET", "/api/game", null);
            assert initial.statusCode() == 200;
            assert initial.body().contains("\"currentTurn\":\"WHITE\"");
            assert game.getGame().pieces().size() == 32;

            var invalid = send(client, base, "POST", "/api/moves", "origin=e2&destination=e5");
            assert invalid.statusCode() == 422 : invalid.body();
            assert game.getGame().equals(original) : "Rejected moves must preserve board and turn";
            var malformed = send(client, base, "POST", "/api/moves", "origin=z9&destination=e4");
            assert malformed.statusCode() == 400;
            assert send(client, base, "POST", "/api/moves", "origin=e2").statusCode() == 400;
            assert send(client, base, "POST", "/api/moves", "origin=e2&origin=e3&destination=e4").statusCode() == 400;
            assert send(client, base, "POST", "/api/moves", "origin=%GG&destination=e4").statusCode() == 400;
            assert game.getGame().equals(original);

            var valid = send(client, base, "POST", "/api/moves", "origin=e2&destination=e4");
            assert valid.statusCode() == 200 : valid.body();
            assert valid.body().contains("\"reason\":null") : "Success has no rejection reason";
            assert game.getGame().currentTurn() == Color.BLACK;
            assert game.getGame().pieces().stream().anyMatch(p -> p.row() == 4 && p.column() == 4 && p.type() == PieceType.PAWN);
            assert original.pieces().stream().anyMatch(p -> p.row() == 6 && p.column() == 4) : "Snapshots must not mutate";

            assert send(client, base, "GET", "/api/moves", null).statusCode() == 405;
            assert send(client, base, "GET", "/api/unknown", null).statusCode() == 404;
            var unsupported = client.send(HttpRequest.newBuilder(URI.create(base + "/api/moves"))
                    .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .build(), HttpResponse.BodyHandlers.ofString());
            assert unsupported.statusCode() == 415;
            assert send(client, base, "POST", "/api/moves", "x".repeat(1025)).statusCode() == 413;

            assert send(client, base, "POST", "/api/game/new", "").statusCode() == 200;
            assert game.getGame().equals(original) : "Reset must restore initial board and turn";
            // Fool's mate verifies that state comes from Java and ended games reject further moves.
            assert send(client, base, "POST", "/api/moves", "origin=f2&destination=f3").statusCode() == 200;
            assert send(client, base, "POST", "/api/moves", "origin=e7&destination=e5").statusCode() == 200;
            assert send(client, base, "POST", "/api/moves", "origin=g2&destination=g4").statusCode() == 200;
            var mate = send(client, base, "POST", "/api/moves", "origin=d8&destination=h4");
            assert mate.statusCode() == 200;
            assert mate.body().contains("\"state\":\"CHECKMATE\"") : mate.body();
            assert send(client, base, "POST", "/api/moves", "origin=a2&destination=a3").statusCode() == 422;
        }
        System.out.println("HTTP integration tests passed!");
    }

    private static HttpResponse<String> send(HttpClient client, String base, String method,
            String path, String body) throws Exception {
        var request = HttpRequest.newBuilder(URI.create(base + path)).timeout(Duration.ofSeconds(5));
        if (body != null) request.header("Content-Type", "application/x-www-form-urlencoded");
        request.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
