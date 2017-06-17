package io.jz.poc.appserver;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MimeTypes {

    protected static final Map<String, String> contentTypes =
            new ConcurrentHashMap<String, String>();

    static {
        {
            addContentType("application/font-woff", "woff");
            addContentType("application/font-woff2", "woff2");
            addContentType("application/java-archive", "jar");
            addContentType("application/javascript", "js");
            addContentType("application/json", "json");
            addContentType("application/octet-stream", "exe");
            addContentType("application/pdf", "pdf");
            addContentType("application/x-7z-compressed", "7z");
            addContentType("application/x-compressed", "tgz");
            addContentType("application/x-gzip", "gz");
            addContentType("application/x-tar", "tar");
            addContentType("application/xhtml+xml", "xhtml");
            addContentType("application/zip", "zip");
            addContentType("audio/mpeg", "mp3");
            addContentType("image/gif", "gif");
            addContentType("image/jpeg", "jpg", "jpeg");
            addContentType("image/png", "png");
            addContentType("image/svg+xml", "svg");
            addContentType("image/x-icon", "ico");
            addContentType("text/css", "css");
            addContentType("text/csv", "csv");
            addContentType("text/html; charset=utf-8", "htm", "html");
            addContentType("text/plain", "txt", "text", "log");
            addContentType("text/xml", "xml");
        }
    }

    public static void addContentType(String contentType, String... suffixes) {
        for (String suffix : suffixes)
            contentTypes.put(suffix.toLowerCase(Locale.US), contentType.toLowerCase(Locale.US));
    }


    public static String getContentType(String path, String def) {
        int dot = path.lastIndexOf('.');
        String type = dot < 0 ? def : contentTypes.get(path.substring(dot + 1).toLowerCase(Locale.US));
        return type != null ? type : def;
    }
}
