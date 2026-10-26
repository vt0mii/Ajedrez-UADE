package com.uade.ajedrez.infrastructure.adapter.in.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.uade.ajedrez.application.dto.MoveCommand;
import com.uade.ajedrez.application.dto.NewGameCommand;
import com.uade.ajedrez.application.exception.GameNotFoundException;
import com.uade.ajedrez.application.exception.InvalidCommandException;
import com.uade.ajedrez.application.exception.MoveRejectedException;
import com.uade.ajedrez.application.port.in.ChessGameUseCases;
import com.uade.ajedrez.domain.exception.InvalidMoveInputException;
import com.uade.ajedrez.domain.port.out.GameLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Adaptador de entrada HTTP basado en el servidor que trae el JDK. Traduce
 * requests a casos de uso y sus resultados a JSON; no conoce ninguna regla del
 * juego.
 */
public class HttpGameServer {

    private final ChessGameUseCases useCases;
    private final GameLogger logger;
    private final int port;

    private HttpServer server;

    public HttpGameServer(ChessGameUseCases useCases, GameLogger logger, int port) {
        this.useCases = useCases;
        this.logger = logger;
        this.port = port;
    }

    /**
     * Arranca el servidor.
     *
     * @return el puerto real, útil cuando se pide el puerto 0 en los tests.
     */
    public int start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", this::handle);
        server.start();

        int actualPort = server.getAddress().getPort();
        logger.info("HTTP adapter listening on http://localhost:" + actualPort);
        return actualPort;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        try {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                respondEmpty(exchange, 204);
                return;
            }
            route(exchange);
        } catch (HttpError error) {
            sendError(exchange, error.getStatus(), error.getCode(), error.getMessage());
        } catch (GameNotFoundException error) {
            sendError(exchange, 404, "GAME_NOT_FOUND", error.getMessage());
        } catch (MoveRejectedException error) {
            sendError(exchange, 409, error.getReason().name(), error.getMessage());
        } catch (InvalidCommandException | InvalidMoveInputException error) {
            sendError(exchange, 400, "INVALID_INPUT", error.getMessage());
        } catch (RuntimeException error) {
            logger.error("Unhandled error in the HTTP adapter", error);
            sendError(exchange, 500, "INTERNAL_ERROR", "Unexpected error");
        }
    }

    private void route(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length == 3 && parts[2].equals("health")) {
            requireMethod(method, "GET");
            respondJson(exchange, 200, "{\"status\":\"ok\"}");
            return;
        }

        if (parts.length == 3 && parts[2].equals("games")) {
            requireMethod(method, "POST");
            createGame(exchange);
            return;
        }

        if (parts.length == 4 && parts[2].equals("games")) {
            requireMethod(method, "GET");
            respondJson(exchange, 200, GameViewJson.toJson(useCases.getGame(parts[3])));
            return;
        }

        if (parts.length == 5 && parts[2].equals("games")) {
            routeGameAction(exchange, method, parts[3], parts[4]);
            return;
        }

        throw new HttpError(404, "NOT_FOUND", "Unknown route: " + path);
    }

    private void routeGameAction(HttpExchange exchange, String method, String gameId, String action)
            throws IOException {
        switch (action) {
            case "moves" -> {
                requireMethod(method, "POST");
                makeMove(exchange, gameId);
            }
            case "abandon" -> {
                requireMethod(method, "POST");
                respondJson(exchange, 200, GameViewJson.toJson(useCases.abandonGame(gameId)));
            }
            case "legal-moves" -> {
                requireMethod(method, "GET");
                legalMoves(exchange, gameId);
            }
            default -> throw new HttpError(404, "NOT_FOUND", "Unknown game action: " + action);
        }
    }

    private void createGame(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        NewGameCommand command = new NewGameCommand(
                JsonBody.stringField(body, "whitePlayerName"),
                JsonBody.stringField(body, "blackPlayerName"));
        respondJson(exchange, 201, GameViewJson.toJson(useCases.createGame(command)));
    }

    private void makeMove(HttpExchange exchange, String gameId) throws IOException {
        String body = readBody(exchange);
        MoveCommand command = new MoveCommand(required(body, "from"), required(body, "to"));
        respondJson(exchange, 200, GameViewJson.toJson(useCases.makeMove(gameId, command)));
    }

    private void legalMoves(HttpExchange exchange, String gameId) throws IOException {
        String from = queryParameter(exchange.getRequestURI(), "from");
        if (from == null || from.isBlank()) {
            throw new HttpError(400, "INVALID_INPUT", "Missing query parameter: from");
        }

        List<String> moves = useCases.legalMoves(gameId, from);
        respondJson(exchange, 200, "{\"moves\":" + Json.stringArray(moves) + "}");
    }

    private static String required(String body, String field) {
        String value = JsonBody.stringField(body, field);
        if (value == null || value.isBlank()) {
            throw new HttpError(400, "INVALID_INPUT", "Missing field: " + field);
        }
        return value;
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static String queryParameter(URI uri, String name) {
        String query = uri.getRawQuery();
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            int separator = pair.indexOf('=');
            if (separator > 0 && pair.substring(0, separator).equals(name)) {
                return URLDecoder.decode(pair.substring(separator + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private static void requireMethod(String method, String expected) {
        if (!method.equals(expected)) {
            throw new HttpError(405, "METHOD_NOT_ALLOWED", "Method not allowed: " + method);
        }
    }

    private static void respondJson(HttpExchange exchange, int status, String json) throws IOException {
        setCors(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    private static void respondEmpty(HttpExchange exchange, int status) throws IOException {
        setCors(exchange);
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }

    private static void sendError(HttpExchange exchange, int status, String code, String message)
            throws IOException {
        respondJson(exchange, status,
                "{\"error\":" + Json.quote(code) + ",\"message\":" + Json.quote(message) + "}");
    }

    private static void setCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
}
