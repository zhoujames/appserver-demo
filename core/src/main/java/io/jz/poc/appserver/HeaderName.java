package io.jz.poc.appserver;

public enum HeaderName {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-length"),
    DATE("Date"),
    CONNECTION("Connection"),
    HOST("Host"),
    SERVER("Server");

    private String header;

    HeaderName(String name) {
        this.header = name;
    }

    public String header() {
        return header;
    }
}
