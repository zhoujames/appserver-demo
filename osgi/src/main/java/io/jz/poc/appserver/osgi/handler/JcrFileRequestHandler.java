package io.jz.poc.appserver.osgi.handler;

import io.jz.poc.appserver.HttpStatusCode;
import io.jz.poc.appserver.MimeTypes;
import io.jz.poc.appserver.Request;
import io.jz.poc.appserver.Response;
import io.jz.poc.appserver.config.Context;
import io.jz.poc.appserver.config.VirtualHost;
import io.jz.poc.appserver.handler.RequestHandler;
import io.jz.poc.appserver.osgi.LightweightHttpServerService;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;

import static io.jz.poc.appserver.AppServerConfig.DEFAULT_CONTENT_TYPE;
import static io.jz.poc.appserver.util.SocketUtils.transfer;

public class JcrFileRequestHandler extends RequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JcrFileRequestHandler.class);

    public HttpStatusCode process(VirtualHost virtualHost, Context context, Request request, Response response) throws IOException{
        String relativePath = "/";
        if (context.getAlias() != null) {
            relativePath = context.getPath()+"/"+request.getPath().substring(context.getAlias().length());
        } else if (context.getPath() != null) {
            relativePath = request.getPath().substring(context.getPath().length());
        }

        String root = virtualHost.getDocumentRoot();
        final BundleContext bundleContext = FrameworkUtil.getBundle(LightweightHttpServerService.class).getBundleContext();
        final ServiceReference<SlingRepository> serviceReference =
                bundleContext.getServiceReference(SlingRepository.class);
        final SlingRepository service = bundleContext.getService(serviceReference);
        Session session = null;
        try {
            session = service.loginAdministrative(null);
            String filePath = root + "/" + relativePath;
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }
            if (!session.getRootNode().hasNode(filePath)){
                return HttpStatusCode.NOT_FOUND;
            } else {
                copy(relativePath,session.getRootNode().getNode(filePath).getNode("jcr:content").getProperty("jcr:data").getStream(), response);
                return HttpStatusCode.OK;
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
            return HttpStatusCode.INTERNAL_SERVER_ERROR;
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }


    public static void copy(String name, InputStream is, Response resp) throws IOException {
        long len = is.available();
        resp.sendServerHeaders(HttpStatusCode.OK, len, MimeTypes.getContentType(name, DEFAULT_CONTENT_TYPE));
        try {
            transfer(is, resp.out(), len);
        } finally {
            is.close();
        }
    }
}
