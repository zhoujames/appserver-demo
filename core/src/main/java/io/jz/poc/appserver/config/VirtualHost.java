package io.jz.poc.appserver.config;

import io.jz.poc.appserver.HttpMethod;
import io.jz.poc.appserver.handler.RequestHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualHost {

    private String name;

    private List<Context> contexts;

    private String documentRoot;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Context> getContexts() {
        return contexts;
    }

    public void setContexts(List<Context> contexts) {
        this.contexts = contexts;
    }

    public String getDocumentRoot() {
        return documentRoot;
    }

    public void setDocumentRoot(String documentRoot) {
        this.documentRoot = documentRoot;
    }

    public Context match(String path) {
        if (contexts == null && contexts.size() == 0) {
            return null;
        }
        for (Context context : contexts) {
            if (context.getAlias() != null) {
                if (path.startsWith(context.getAlias())){
                    return context;
                }
            } else {
                if (path.startsWith(context.getPath())){
                    return context;
                }
            }

        }
        return null;
    }

}
