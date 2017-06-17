package io.jz.poc.appserver;

public class Header {

    private final String name;
    private String value;

    public Header(String name, String value) {
        this.name = name.trim();
        this.value = value.trim();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
