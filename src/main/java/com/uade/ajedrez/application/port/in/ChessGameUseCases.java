package com.uade.ajedrez.application.port.in;

/**
 * Agrupación de los casos de uso para que los adaptadores de entrada dependan
 * de un solo objeto, sin perder las interfaces individuales.
 */
public interface ChessGameUseCases extends CreateGameUseCase, GetGameUseCase,
        MakeMoveUseCase, LegalMovesUseCase, AbandonGameUseCase {
}
