package io.jz.poc.appserver.plugin;

import com.google.common.collect.Lists;
import io.jz.poc.appserver.AppServerConfig;
import io.jz.poc.appserver.HttpMethod;
import io.jz.poc.appserver.config.Context;
import io.jz.poc.appserver.config.VirtualHost;
import io.jz.poc.appserver.handler.FileRequestHandler;

public class DefaultVirtualHost extends VirtualHost{

    public DefaultVirtualHost(String documentRoot) {
        this.setDocumentRoot(documentRoot);
        this.setName(AppServerConfig.DEFAULT_HOST);
        Context defaultContext = new Context();
        defaultContext.setPath("/");
        defaultContext.add(HttpMethod.GET, new FileRequestHandler());
        this.setContexts(Lists.newArrayList(defaultContext));
    }
}
