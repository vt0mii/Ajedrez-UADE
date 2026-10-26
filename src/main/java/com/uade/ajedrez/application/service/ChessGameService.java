package com.uade.ajedrez.application.service;

import com.uade.ajedrez.application.dto.GameView;
import com.uade.ajedrez.application.dto.MoveCommand;
import com.uade.ajedrez.application.dto.NewGameCommand;
import com.uade.ajedrez.application.exception.GameNotFoundException;
import com.uade.ajedrez.application.exception.InvalidCommandException;
import com.uade.ajedrez.application.exception.MoveRejectedException;
import com.uade.ajedrez.application.port.in.ChessGameUseCases;
import com.uade.ajedrez.application.port.out.GameRepositoryPort;
import com.uade.ajedrez.domain.model.*;
import com.uade.ajedrez.domain.port.out.GameLogger;
import com.uade.ajedrez.domain.service.MovementService;

import java.util.List;
import java.util.UUID;

// Caso de uso principal: coordina el dominio con el repositorio y el logger.
// No sabe nada de consola, HTTP ni archivos.
public class ChessGameService implements ChessGameUseCases {

    private static final int MAX_PLAYER_NAME_LENGTH = 40;

    private final GameRepositoryPort repository;
    private final MovementService movementService;
    private final GameLogger logger;

    public ChessGameService(GameRepositoryPort repository, MovementService movementService) {
        this(repository, movementService, GameLogger.noop());
    }

    public ChessGameService(GameRepositoryPort repository, MovementService movementService,
            GameLogger logger) {
        this.repository = repository;
        this.movementService = movementService;
        this.logger = logger;
    }

    @Override
    public GameView createGame(NewGameCommand command) {
        if (command == null) {
            throw new InvalidCommandException("Missing game configuration");
        }

        String whiteName = validPlayerName(command.whitePlayerName(), "White");
        String blackName = validPlayerName(command.blackPlayerName(), "Black");

        GameId id = new GameId("g_" + UUID.randomUUID().toString().replace("-", ""));
        Player whitePlayer = new Player(whiteName, Color.WHITE);
        Player blackPlayer = new Player(blackName, Color.BLACK);

        ChessGame game = ChessGame.newGame(id, whitePlayer, blackPlayer, new Board(), movementService);
        repository.save(game);
        logger.info("Game created: " + id.getValue());
        return GameViewMapper.toView(game);
    }

    @Override
    public GameView getGame(String gameId) {
        return GameViewMapper.toView(findGame(gameId));
    }

    @Override
    public GameView makeMove(String gameId, MoveCommand command) {
        if (command == null) {
            throw new InvalidCommandException("Missing move");
        }

        ChessGame game = findGame(gameId);
        Move move = new Move(parsePosition(command.from()), parsePosition(command.to()));
        Color mover = game.getCurrentTurn();

        MoveResult result = game.makeMove(move);
        if (!result.wasPossible()) {
            logger.warning("Move rejected: " + result.getMessage());
            throw new MoveRejectedException(result.getReason(), result.getMessage());
        }

        repository.save(game);
        logger.info("Move completed by " + mover + "; state=" + game.getState());
        return GameViewMapper.toView(game);
    }

    @Override
    public List<String> legalMoves(String gameId, String fromSquare) {
        ChessGame game = findGame(gameId);
        Position origin = parsePosition(fromSquare);
        return game.legalMovesFrom(origin);
    }

    @Override
    public GameView abandonGame(String gameId) {
        ChessGame game = findGame(gameId);
        game.abandon();
        repository.save(game);
        logger.info("Game abandoned: " + gameId);
        return GameViewMapper.toView(game);
    }

    private ChessGame findGame(String gameId) {
        GameId id = parseGameId(gameId);
        return repository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id.getValue()));
    }

    private GameId parseGameId(String gameId) {
        try {
            return new GameId(gameId);
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException("Invalid game id: " + gameId);
        }
    }

    private Position parsePosition(String coordinate) {
        return Position.fromAlgebraic(coordinate);
    }

    private String validPlayerName(String name, String defaultName) {
        String candidate = name == null ? "" : name.trim();
        if (candidate.isEmpty()) {
            candidate = defaultName;
        }
        if (candidate.length() > MAX_PLAYER_NAME_LENGTH) {
            throw new InvalidCommandException("Player name is too long (max " + MAX_PLAYER_NAME_LENGTH + ")");
        }
        return candidate;
    }
}
