package io.jz.poc.appserver.osgi.plugin;

import io.jz.poc.appserver.HttpMethod;
import io.jz.poc.appserver.config.Context;
import io.jz.poc.appserver.osgi.handler.JcrFileRequestHandler;

public class JcrContext extends Context {

    public JcrContext() {
        this.setType("jcr");
        this.add(HttpMethod.GET,new JcrFileRequestHandler());
    }
}
