package io.jz.poc.appserver;

public enum HttpStatusCode {
    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWS(405),
    INTERNAL_SERVER_ERROR(500);

    private int statusCode;

    HttpStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int code() {
        return statusCode;
    }
}
