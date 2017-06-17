package io.jz.poc.appserver;

import io.jz.poc.appserver.config.VirtualHost;

import java.io.Serializable;
import java.util.List;

public class AppServerConfig implements Serializable {

    public static final String DEFAULT_CONTENT_TYPE = "text/html; charset=utf-8";
    public static final String DEFAULT_DATE_FORMAT = "E,dd MMM yyyy HH:mm:ss 'GMT'";
    public static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    public static final String DEFAULT_ERROR_HTML = "<!DOCTYPE html><html><head><title>%d %s</title></head><body>%d %s</body></html>";
    public static final String STRING_CLOSE = "close";
    public static final String DEFAULT_HOST = "~DEFAULT~";
    public static final String APP_SERVER_DEMO = "AppServer Demo";
    public static final String STRING_KEEP_ALIVE = "Keep-Alive";


    private int port;

    private int socketTimeout;

    public int getPort() {
        return port;
    }

    private String documentRoot = "/html";

    public int getSocketTimeout() {
        return socketTimeout;
    }

    private List<VirtualHost> virtualHosts;

    public String getDocumentRoot() {
        return documentRoot;
    }

    public void setDocumentRoot(String documentRoot) {
        this.documentRoot = documentRoot;
    }

    public List<VirtualHost> getVirtualHosts() {
        return virtualHosts;
    }

    public void setVirtualHosts(List<VirtualHost> virtualHosts) {
        this.virtualHosts = virtualHosts;
    }
}
