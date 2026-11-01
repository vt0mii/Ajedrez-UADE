package com.uade.ajedrez.application.port.in;

// Todos los casos de uso juntos. Los adaptadores dependen de esta interfaz
// y no tienen que inyectarse un montón de objetos.
public interface ChessGameUseCases extends CreateGameUseCase, GetGameUseCase,
        MakeMoveUseCase, LegalMovesUseCase, AbandonGameUseCase, ReplayGameUseCase,
        ListGamesUseCase, SaveGameUseCase {
}
