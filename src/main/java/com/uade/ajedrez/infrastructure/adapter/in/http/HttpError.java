package com.uade.ajedrez.infrastructure.adapter.in.http;

// Error del adaptador HTTP con el status y el código que van en el body.
final class HttpError extends RuntimeException {

    private final int status;
    private final String code;

    HttpError(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    int getStatus() {
        return status;
    }

    String getCode() {
        return code;
    }
}
