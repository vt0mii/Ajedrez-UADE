package com.uade.ajedrez.infrastructure.adapter.in.http;

import com.uade.ajedrez.application.service.ChessGameService;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.domain.service.MovementService;
import com.uade.ajedrez.infrastructure.adapter.out.persistence.InMemoryGameRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpGameServerTest {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static String baseUrl;
    private static String rootUrl;

    public static void main(String[] args) throws Exception {
        System.out.println("Ejecutando tests del adaptador HTTP...\n");

        HttpGameServer server = new HttpGameServer(
                new ChessGameService(new InMemoryGameRepository(), new MovementService()),
                GameLogger.noop(),
                0);
        int port = server.start();
        baseUrl = "http://localhost:" + port + "/api";
        rootUrl = "http://localhost:" + port;

        try {
            testHealth();
            testCrearPartidaYMover();
            testMovimientoIlegal();
            testPartidaInexistente();
            testListarPartidas();
            testGuardarPartida();
            testReplay();
            testRutaDesconocida();
            testRaiz();
        } finally {
            server.stop();
        }

        System.out.println("\nTodos los tests del adaptador HTTP pasaron!");
    }

    static void testHealth() throws Exception {
        HttpResponse<String> response = get("/health");

        assert response.statusCode() == 200 : "health deberia responder 200";
        assert response.body().contains("ok") : "health deberia confirmar el estado";

        System.out.println("V testHealth paso");
    }

    static void testCrearPartidaYMover() throws Exception {
        HttpResponse<String> created = post("/games",
                "{\"whitePlayerName\":\"Ana\",\"blackPlayerName\":\"Beto\"}");

        assert created.statusCode() == 201 : "crear partida deberia responder 201";
        String gameId = JsonBody.stringField(created.body(), "id");
        assert gameId != null && gameId.startsWith("g_") : "la respuesta deberia incluir el id";
        assert created.body().contains("\"whitePlayer\":{\"name\":\"Ana\",\"color\":\"WHITE\"}")
                : "la respuesta deberia incluir al jugador blanco";

        HttpResponse<String> legal = get("/games/" + gameId + "/legal-moves?from=e2");
        assert legal.statusCode() == 200 : "legal-moves deberia responder 200";
        assert legal.body().contains("\"e4\"") : "e2 deberia poder ir a e4";

        HttpResponse<String> moved = post("/games/" + gameId + "/moves",
                "{\"from\":\"e2\",\"to\":\"e4\"}");
        assert moved.statusCode() == 200 : "el movimiento deberia aceptarse";
        assert moved.body().contains("\"turn\":\"BLACK\"") : "el turno deberia pasar a negras";

        HttpResponse<String> reloaded = get("/games/" + gameId);
        assert reloaded.statusCode() == 200 : "la partida guardada deberia existir";
        assert reloaded.body().contains("\"lastMove\":{\"from\":\"e2\",\"to\":\"e4\"}")
                : "el historial deberia conservar el movimiento";

        System.out.println("V testCrearPartidaYMover paso");
    }

    static void testMovimientoIlegal() throws Exception {
        String gameId = JsonBody.stringField(createGame().body(), "id");

        HttpResponse<String> rejected = post("/games/" + gameId + "/moves",
                "{\"from\":\"e2\",\"to\":\"e5\"}");

        assert rejected.statusCode() == 409 : "un movimiento ilegal deberia responder 409";
        assert rejected.body().contains("\"error\":\"NOT_AVAILABLE\"")
                : "el error deberia conservar el motivo del dominio";

        System.out.println("V testMovimientoIlegal paso");
    }

    static void testPartidaInexistente() throws Exception {
        HttpResponse<String> missing = get("/games/g_inexistente");

        assert missing.statusCode() == 404 : "una partida inexistente deberia responder 404";
        assert missing.body().contains("\"error\":\"GAME_NOT_FOUND\"")
                : "el error deberia ser GAME_NOT_FOUND";

        System.out.println("V testPartidaInexistente paso");
    }

    static void testListarPartidas() throws Exception {
        HttpResponse<String> created = post("/games",
                "{\"whitePlayerName\":\"Lista\",\"blackPlayerName\":\"Resumen\"}");
        assert created.statusCode() == 201 : "crear partida deberia responder 201";

        HttpResponse<String> listed = get("/games");

        assert listed.statusCode() == 200 : "listar partidas deberia responder 200";
        assert listed.body().startsWith("{\"games\":[") : "deberia devolver un array de partidas";
        assert listed.body().contains("\"whitePlayer\":\"Lista\"")
                : "el resumen deberia incluir al jugador blanco";
        assert listed.body().contains("\"moveCount\":0") : "el resumen deberia incluir la cantidad de jugadas";

        System.out.println("V testListarPartidas paso");
    }

    static void testGuardarPartida() throws Exception {
        String gameId = JsonBody.stringField(createGame().body(), "id");
        post("/games/" + gameId + "/moves", "{\"from\":\"d2\",\"to\":\"d4\"}");

        HttpResponse<String> saved = post("/games/" + gameId + "/save", "");

        assert saved.statusCode() == 200 : "guardar deberia responder 200";
        assert saved.body().contains("\"lastMove\":{\"from\":\"d2\",\"to\":\"d4\"}")
                : "guardar deberia devolver la partida completa";

        HttpResponse<String> missing = post("/games/g_inexistente/save", "");
        assert missing.statusCode() == 404 : "guardar una partida inexistente deberia responder 404";

        System.out.println("V testGuardarPartida paso");
    }

    static void testReplay() throws Exception {
        String gameId = JsonBody.stringField(createGame().body(), "id");
        post("/games/" + gameId + "/moves", "{\"from\":\"e2\",\"to\":\"e4\"}");
        post("/games/" + gameId + "/abandon", "");

        HttpResponse<String> replay = get("/games/" + gameId + "/replay?position=1");

        assert replay.statusCode() == 200 : "el replay deberia responder 200";
        assert replay.body().contains("\"position\":1") : "deberia devolver la posicion pedida";
        assert replay.body().contains("\"totalMoves\":1") : "deberia informar el total de jugadas";
        assert replay.body().contains("\"result\":\"ABANDONED\"") : "deberia conservar el resultado";
        assert replay.body().contains("\"turn\":\"BLACK\"") : "el tablero deberia ser el de la jugada 1";

        HttpResponse<String> invalidPosition = get("/games/" + gameId + "/replay?position=abc");
        assert invalidPosition.statusCode() == 400 : "una posicion invalida deberia responder 400";

        String activeId = JsonBody.stringField(createGame().body(), "id");
        HttpResponse<String> rejected = get("/games/" + activeId + "/replay");
        assert rejected.statusCode() == 400 : "una partida activa no deberia tener replay";

        System.out.println("V testReplay paso");
    }

    static void testRutaDesconocida() throws Exception {
        HttpResponse<String> unknown = get("/nope");

        assert unknown.statusCode() == 404 : "una ruta desconocida deberia responder 404";

        System.out.println("V testRutaDesconocida paso");
    }

    static void testRaiz() throws Exception {
        HttpResponse<String> root = getFrom(rootUrl + "/");

        assert root.statusCode() == 200 : "la raiz deberia responder 200";
        assert root.body().contains("http://localhost:3000")
                : "la raiz deberia indicar donde esta el frontend";

        HttpResponse<String> other = getFrom(rootUrl + "/favicon.ico");
        assert other.statusCode() == 404 : "una ruta fuera de /api deberia responder 404";
        assert other.body().contains("\"error\":\"NOT_FOUND\"")
                : "la ruta desconocida deberia devolver JSON";

        System.out.println("V testRaiz paso");
    }

    private static HttpResponse<String> createGame() throws Exception {
        return post("/games", "{\"whitePlayerName\":\"Ana\",\"blackPlayerName\":\"Beto\"}");
    }

    private static HttpResponse<String> get(String path) throws Exception {
        return getFrom(baseUrl + path);
    }

    private static HttpResponse<String> getFrom(String url) throws Exception {
        return CLIENT.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> post(String path, String body) throws Exception {
        return CLIENT.send(HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
                HttpResponse.BodyHandlers.ofString());
    }
}
