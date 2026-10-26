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

    public static void main(String[] args) throws Exception {
        System.out.println("Ejecutando tests del adaptador HTTP...\n");

        HttpGameServer server = new HttpGameServer(
                new ChessGameService(new InMemoryGameRepository(), new MovementService()),
                GameLogger.noop(),
                0);
        baseUrl = "http://localhost:" + server.start() + "/api";

        try {
            testHealth();
            testCrearPartidaYMover();
            testMovimientoIlegal();
            testPartidaInexistente();
            testRutaDesconocida();
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

    static void testRutaDesconocida() throws Exception {
        HttpResponse<String> unknown = get("/nope");

        assert unknown.statusCode() == 404 : "una ruta desconocida deberia responder 404";

        System.out.println("V testRutaDesconocida paso");
    }

    private static HttpResponse<String> createGame() throws Exception {
        return post("/games", "{\"whitePlayerName\":\"Ana\",\"blackPlayerName\":\"Beto\"}");
    }

    private static HttpResponse<String> get(String path) throws Exception {
        return CLIENT.send(HttpRequest.newBuilder(URI.create(baseUrl + path)).GET().build(),
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
