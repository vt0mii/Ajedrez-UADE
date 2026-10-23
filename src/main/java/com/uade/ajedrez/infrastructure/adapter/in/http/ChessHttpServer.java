package com.uade.ajedrez.infrastructure.adapter.in.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.port.in.ChessGame;
import com.uade.ajedrez.domain.port.out.GameLogger;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** HTTP/JSON is confined to this incoming adapter. Binds to local machine only. */
public class ChessHttpServer implements AutoCloseable {
    private final HttpServer server;
    private final ChessGame game;
    private final GameLogger logger;

    public ChessHttpServer(int port, ChessGame game, GameLogger logger) throws IOException {
        this.game = game;
        this.logger = logger;
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api/", this::handle);
    }

    public void start() { server.start(); }
    public int getPort() { return server.getAddress().getPort(); }
    @Override
    public void close() { server.stop(0); }

    private void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if (path.equals("/api/game")) {
                if (!method.equals("GET")) { methodNotAllowed(exchange, "GET"); return; }
                send(exchange, 200, snapshot(game.getGame()));
            } else if (path.equals("/api/game/new")) {
                if (!method.equals("POST")) { methodNotAllowed(exchange, "POST"); return; }
                send(exchange, 200, snapshot(game.newGame()));
            } else if (path.equals("/api/moves")) {
                if (!method.equals("POST")) { methodNotAllowed(exchange, "POST"); return; }
                String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
                if (contentType == null || !contentType.split(";")[0].trim()
                        .equalsIgnoreCase("application/x-www-form-urlencoded")) {
                    send(exchange, 415, error("Expected application/x-www-form-urlencoded"));
                    return;
                }
                byte[] body = exchange.getRequestBody().readNBytes(1025);
                if (body.length > 1024) { send(exchange, 413, error("Request too large")); return; }
                Map<String, String> fields = form(new String(body, StandardCharsets.UTF_8));
                ChessGame.MoveOutcome outcome = game.move(new Move(
                        Position.fromAlgebraic(fields.get("origin")),
                        Position.fromAlgebraic(fields.get("destination"))));
                MoveResult result = outcome.result();
                send(exchange, result.wasPossible() ? 200 : 422,
                        "{\"message\":" + quote(result.getMessage()) + ",\"reason\":"
                        + quote(result.getReason() == null ? null : result.getReason().name())
                        + ",\"game\":" + snapshot(outcome.game()) + "}");
            } else {
                send(exchange, 404, error("Endpoint not found"));
            }
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid HTTP request: " + e.getMessage());
            send(exchange, 400, error(e.getMessage()));
        } catch (RuntimeException e) {
            logger.error("HTTP request failed", e);
            send(exchange, 500, error("Unexpected server error. See server logs."));
        } finally {
            exchange.close();
        }
    }

    private static Map<String, String> form(String body) {
        Map<String, String> fields = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length != 2) { throw new IllegalArgumentException("Invalid form body"); }
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            if (!key.equals("origin") && !key.equals("destination")) {
                throw new IllegalArgumentException("Unknown move field");
            }
            if (fields.putIfAbsent(key, value) != null) {
                throw new IllegalArgumentException("Duplicate move field");
            }
        }
        return fields;
    }

    private static void methodNotAllowed(HttpExchange exchange, String allowed) throws IOException {
        exchange.getResponseHeaders().set("Allow", allowed);
        send(exchange, 405, error("Method not allowed"));
    }

    private static void send(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    private static String error(String message) { return "{\"message\":" + quote(message) + "}"; }

    private static String snapshot(GameSnapshot game) {
        String pieces = game.pieces().stream().map(piece -> "{\"row\":" + piece.row()
                + ",\"column\":" + piece.column() + ",\"type\":" + quote(piece.type().name())
                + ",\"color\":" + quote(piece.color().name()) + "}").collect(Collectors.joining(","));
        return "{\"pieces\":[" + pieces + "],\"currentTurn\":" + quote(game.currentTurn().name())
                + ",\"turnNumber\":" + game.turnNumber() + ",\"state\":" + quote(game.state().name()) + "}";
    }

    private static String quote(String text) {
        if (text == null) { return "null"; }
        StringBuilder result = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            if (c == '"' || c == '\\') { result.append('\\').append(c); }
            else if (c < 32) { result.append(String.format("\\u%04x", (int) c)); }
            else { result.append(c); }
        }
        return result.append('"').toString();
    }
}
