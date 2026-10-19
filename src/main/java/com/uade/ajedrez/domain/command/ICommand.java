package com.uade.ajedrez.domain.command;

public interface ICommand {
    void execute();

    void undo();
}
