package io.jz.poc.appserver.handler;

import io.jz.poc.appserver.HttpStatusCode;
import io.jz.poc.appserver.Request;
import io.jz.poc.appserver.Response;
import io.jz.poc.appserver.config.VirtualHost;
import io.jz.poc.appserver.plugin.DefaultVirtualHost;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileRequestHandlerTest {

    @Test
    public void processWithNotFound() throws Exception {
        FileRequestHandler fileRequestHandler = new FileRequestHandler();
        VirtualHost virtualHost = new DefaultVirtualHost("/documentRoot");
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        when(request.getPath()).thenReturn("/test.txt");
        final HttpStatusCode process = fileRequestHandler.process(virtualHost, virtualHost.getContexts().get(0), request, response);
        assertTrue(process.equals(HttpStatusCode.NOT_FOUND));
    }

    @Test
    public void processWithIndex() throws Exception {
        FileRequestHandler fileRequestHandler = new FileRequestHandler();
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath+"../../target");
        VirtualHost virtualHost = new DefaultVirtualHost(targetDir.getAbsolutePath());
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        when(request.getPath()).thenReturn("/");
        final HttpStatusCode process = fileRequestHandler.process(virtualHost, virtualHost.getContexts().get(0), request, response);
        assertTrue(process.equals(HttpStatusCode.OK));
        verify(response).send(any(HttpStatusCode.class),any(String.class));
    }

    @Test
    public void processWithFile() throws Exception {
        FileRequestHandler fileRequestHandler = new FileRequestHandler();
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath+"../../src/test/resources/");
        VirtualHost virtualHost = new DefaultVirtualHost(targetDir.getAbsolutePath());
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);
        when(request.getPath()).thenReturn("/config-test.json");
        final HttpStatusCode process = fileRequestHandler.process(virtualHost, virtualHost.getContexts().get(0), request, response);
        assertTrue(process.equals(HttpStatusCode.OK));
    }
}