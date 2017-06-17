package io.jz.poc.appserver;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.jz.poc.appserver.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class Request implements Serializable {

    private HttpMethod method;
    private URI uri;
    private URL baseURL;
    private String version;
    private Headers headers = new Headers();
    private InputStream body;
    private Map<String, String> params;


    public Headers getHeaders() {
        return headers;
    }

    public Request(InputStream in) throws IOException {
        //read first line
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        if (Strings.isNullOrEmpty(line)) {
            throw new IOException("missing header");
        }
        ArrayList<String> list = Lists.newArrayList(Splitter.on(" ").split(line));
        if (list.size() != 3) {
            throw new IOException("invalid header");
        }

        this.method = HttpMethod.valueOf(list.get(0));
        if (method == null) {
            throw new IOException("Not Support Method");
        }
        try {
            this.uri = new URI(StringUtils.removeDuplicates(list.get(1), '/'));
        } catch (URISyntaxException e) {
            throw new IOException(String.format("invalid URI %s", e.getMessage()));
        }
        this.version = list.get(2);
        while ((line = br.readLine()).length() > 0) {
            String trimLine = CharMatcher.whitespace().trimFrom(line);
            //TODO: folded header
            int separator = trimLine.indexOf(':');
            if (separator == -1)
                throw new IOException("invalid header: \"" + line + "\"");
            String name = trimLine.substring(0, separator);
            String value = trimLine.substring(separator + 1).trim();
            headers.addHeader(name, value);
        }
        //TODO: chunked stream and Content-length
        body = in;
    }

    public InputStream getBody() {
        return body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public boolean isHttp1_1() {
        return version != null && version.endsWith("1.1");
    }

    public String getPath() {
        return uri.getPath();
    }
}
