package io.jz.poc.appserver.handler;

import io.jz.poc.appserver.HttpStatusCode;
import io.jz.poc.appserver.Request;
import io.jz.poc.appserver.Response;
import io.jz.poc.appserver.config.Context;
import io.jz.poc.appserver.config.VirtualHost;

import java.io.IOException;

public abstract class RequestHandler {
    public abstract HttpStatusCode process(VirtualHost virtualHost, Context context, Request request, Response response) throws IOException;
}
