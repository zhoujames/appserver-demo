package io.jz.poc.appserver.handler;

import io.jz.poc.appserver.*;
import io.jz.poc.appserver.config.Context;
import io.jz.poc.appserver.config.VirtualHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Formatter;
import java.util.Locale;

import static io.jz.poc.appserver.AppServerConfig.DEFAULT_CONTENT_TYPE;
import static io.jz.poc.appserver.util.SocketUtils.transfer;
import static io.jz.poc.appserver.util.StringUtils.getParentPath;
import static io.jz.poc.appserver.util.StringUtils.toSizeApproxString;

public class FileRequestHandler extends RequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileRequestHandler.class);

    public HttpStatusCode process(VirtualHost virtualHost, Context context, Request request, Response response) throws IOException{
        String relativePath = "/";
        if (context.getAlias() != null) {
            relativePath = context.getPath()+"/"+request.getPath().substring(context.getAlias().length());
        } else if (context.getPath() != null) {
            relativePath = request.getPath().substring(context.getPath().length());
        }
        File file = new File(virtualHost.getDocumentRoot(), relativePath).getCanonicalFile();
        if (!file.exists() || file.isHidden() || file.getName().startsWith(".")) {
            return HttpStatusCode.NOT_FOUND;
        } else if (file.isDirectory()) {
            LOGGER.debug("process directory {}",file.getCanonicalPath());
            response.send(HttpStatusCode.OK, createIndex(file, request.getPath()));
            return HttpStatusCode.OK;
        } else {
            LOGGER.debug("process file {}",file.getCanonicalPath());
            copy(file, response);
            return HttpStatusCode.OK;
        }
    }


    /**
     * apache format directory index
     * https://www.freeutils.net/source/jlhttp/
     */
    public static String createIndex(File dir, String path) {
        if (!path.endsWith("/"))
            path += "/";
        // calculate name column width
        int w = 21; // minimum width
        for (String name : dir.list())
            if (name.length() > w)
                w = name.length();
        w += 2; // with room for added slash and space
        // note: we use apache's format, for consistent user experience
        Formatter f = new Formatter(Locale.US);
        f.format("<!DOCTYPE html>%n" +
                        "<html><head><title>Index of %s</title></head>%n" +
                        "<body><h1>Index of %s</h1>%n" +
                        "<pre> Name%" + (w - 5) + "s Last modified      Size<hr>",
                path, path, "");
        if (path.length() > 1) // add parent link if not root path
            f.format(" <a href=\"%s/\">Parent Directory</a>%"
                    + (w + 5) + "s-%n", getParentPath(path), "");
        for (File file : dir.listFiles()) {
            try {
                String name = file.getName() + (file.isDirectory() ? "/" : "");
                String size = file.isDirectory() ? "- " : toSizeApproxString(file.length());
                // properly url-encode the link
                String link = new URI(null, path + name, null).toASCIIString();
                if (!file.isHidden() && !name.startsWith("."))
                    f.format(" <a href=\"%s\">%s</a>%-" + (w - name.length()) +
                                    "s&#8206;%td-%<tb-%<tY %<tR%6s%n",
                            link, name, "", file.lastModified(), size);
            } catch (URISyntaxException ignore) {}
        }
        f.format("</pre></body></html>");
        return f.toString();
    }



    public static void copy(File file, Response resp) throws IOException {
        long len = file.length();
        resp.sendServerHeaders(HttpStatusCode.OK, len, MimeTypes.getContentType(file.getName(), DEFAULT_CONTENT_TYPE));
        FileInputStream fis = new FileInputStream(file);
        try {
            transfer(fis, resp.out(), len);
        } finally {
            fis.close();
        }
    }
}
