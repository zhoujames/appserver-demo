package io.jz.poc.appserver;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import io.jz.poc.appserver.config.Context;
import io.jz.poc.appserver.config.VirtualHost;
import io.jz.poc.appserver.handler.RequestHandler;
import io.jz.poc.appserver.plugin.DefaultVirtualHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.jz.poc.appserver.AppServerConfig.STRING_CLOSE;
import static io.jz.poc.appserver.HttpMethod.GET;
import static io.jz.poc.appserver.util.SocketUtils.transfer;

public class AppServer implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(AppServer.class);


    AppServerConfig config;

    public AppServer(AppServerConfig appServerConfig) {
        this.config = appServerConfig;
        if (config.getVirtualHosts() == null) {
            LOGGER.info("no virtual host defined use default");
            config.setVirtualHosts(Lists.<VirtualHost>newArrayList(new DefaultVirtualHost(config.getDocumentRoot())));
        }
    }

    public void setConfig(AppServerConfig config) {
        this.config = config;
    }

    protected volatile Executor executor;
    protected volatile ServerSocket serverSocket;

    public void start() throws IOException {
        //Reload
        serverSocket = createServerSocket();
        executor = Executors.newCachedThreadPool();
        while (!serverSocket.isClosed()) {
            final Socket sock = serverSocket.accept();
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        sock.setSoTimeout(config.getSocketTimeout());
                        sock.setTcpNoDelay(true); //increase performance
                        process(sock.getInputStream(), sock.getOutputStream());
                    } catch (IOException e) {
                        LOGGER.error("caught exception ",e);
                    } finally {
                        gracefulClose(sock);
                    }
                }
            });
        }
    }

    public void stop(){
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.error("caught exception ",e);
            }
        }
        serverSocket = null;
    }

    private void process(InputStream inputStream, OutputStream outputStream) throws IOException {
        Request req;
        Response resp;
        inputStream = new BufferedInputStream(inputStream, 4096);
        outputStream = new BufferedOutputStream(outputStream, 4096);
        do {
            req = null;
            resp = new Response(outputStream);
            try {
                req = new Request(inputStream);
                resp.setReq(req);//set request to response in order to check request header
                LOGGER.debug("receive request method:{} path:{}",req.getMethod(),req.getPath());
                process(req, resp);
            } catch (Throwable t) {
                if (req == null) {
                    resp.send(HttpStatusCode.BAD_REQUEST);
                } else {
                    resp.send(HttpStatusCode.INTERNAL_SERVER_ERROR);
                }
                break; // proceed to close connection
            } finally {
                if (resp != null) {
                    resp.close();
                }
            }
            LOGGER.debug("Process Finish, consume the leftover body {}",req.getBody().available());
            transfer(req.getBody(), null, -1); //to consume the left over request body if there are any
            // persist connection unless client or server close explicitly
        } while (!req.getHeaders().validate(HeaderName.CONNECTION.header(), STRING_CLOSE)
                && !resp.getHeaders().validate(HeaderName.CONNECTION.header(), STRING_CLOSE) && req.isHttp1_1());

    }

    //find out which virtual host will be used by matching the Host with name of virtual host
    private VirtualHost match(Request request) {
        if (config.getVirtualHosts().size() == 1) {
            return config.getVirtualHosts().get(0);
        }
        for (VirtualHost virtualHost : config.getVirtualHosts()) {
            if (AppServerConfig.DEFAULT_HOST.equalsIgnoreCase(virtualHost.getName())){
                return virtualHost;
            }
            Optional<Header> hostHeader = request.getHeaders().find(HeaderName.HOST.header());
            if (hostHeader.isPresent()) {
                if (virtualHost.getName().equalsIgnoreCase(hostHeader.get().getValue())) {
                    LOGGER.debug("virtual host {} match ",virtualHost.getName());
                    return virtualHost;
                }
            }
        }
        LOGGER.debug("no matched virtual host use the first one");
        return config.getVirtualHosts().get(0);
    }


    private void process(Request req, Response resp) throws IOException{
        switch (req.getMethod()) {
            case GET:
                VirtualHost virtualHost = match(req);
                String path = req.getPath();
                Context context = virtualHost.match(path); //find the correct context definition by matching the request path
                LOGGER.debug("found context path:{} alias:{}",context.getPath(),context.getAlias());
                if (context == null) {
                    resp.send(HttpStatusCode.NOT_FOUND);
                    return;
                }
                RequestHandler handler = context.findRequestHandler(GET);
                if (handler == null) {
                    resp.send(HttpStatusCode.NOT_FOUND);
                    return;
                }
                handler.process(virtualHost,context,req, resp);
                break;
            case POST:
            case HEAD:
            case OPTIONS:
            default:
                resp.send(HttpStatusCode.METHOD_NOT_ALLOWS);
                break;
        }
    }


    private void gracefulClose(Socket sock) {
        try {
            sock.shutdownOutput();
            transfer(sock.getInputStream(), null, -1); // consume input
        } catch (IOException e) {

        } finally {
            try {
                sock.close(); // and finally close socket fully
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected ServerSocket createServerSocket() throws IOException {
        ServerSocketFactory factory = ServerSocketFactory.getDefault();
        ServerSocket serv = factory.createServerSocket();
        serv.setReuseAddress(true);
        serv.bind(new InetSocketAddress(config.getPort()));
        return serv;
    }
}
