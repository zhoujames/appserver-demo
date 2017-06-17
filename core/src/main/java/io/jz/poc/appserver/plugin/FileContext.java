package io.jz.poc.appserver.plugin;

import io.jz.poc.appserver.HttpMethod;
import io.jz.poc.appserver.config.Context;
import io.jz.poc.appserver.handler.FileRequestHandler;

public class FileContext extends Context {

    public FileContext() {
        this.setType("file");
        this.add(HttpMethod.GET,new FileRequestHandler());
    }
}
