package com.uade.ajedrez.domain.port.in;

public interface Input {
    /** Returns null when the input is closed. */
    String readLine();
}
