package io.jz.poc.appserver.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.jz.poc.appserver.HttpMethod;
import io.jz.poc.appserver.handler.RequestHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class")
public class Context {

    private String alias;
    private String type;
    private String path;

    public Context() {
    }

    public String getAlias() {
        return alias;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected final Map<HttpMethod, RequestHandler> handlers =
            new ConcurrentHashMap<HttpMethod, RequestHandler>();


    public void add(HttpMethod get, RequestHandler requestHandler) {
        handlers.put(get, requestHandler);
    }

    public RequestHandler findRequestHandler(HttpMethod method) {
        return handlers.get(method);
    }
}
