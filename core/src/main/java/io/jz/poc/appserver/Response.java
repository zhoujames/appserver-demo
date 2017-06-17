package io.jz.poc.appserver;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import static io.jz.poc.appserver.AppServerConfig.*;
import static io.jz.poc.appserver.util.StringUtils.CRLF;
import static io.jz.poc.appserver.util.StringUtils.getBytes;

public class Response {
    private OutputStream out;
    private Headers headers = new Headers();
    private Request req;

    public Response(OutputStream out) {
        this.out = out;
    }

    public Response(Request req, OutputStream out) {
        this.req = req;
        this.out = out;
    }

    public void setReq(Request req) {
        this.req = req;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void send(HttpStatusCode status) throws IOException {
        send(status, String.format(
                DEFAULT_ERROR_HTML,
                status.code(), status.name(), status.code(), status.name()));
    }


    public void send(HttpStatusCode status, String body) throws IOException {
        byte[] content = body.getBytes(Charsets.UTF_8.name());
        sendServerHeaders(status, content.length, DEFAULT_CONTENT_TYPE);
        out.write(content);
    }

    public void sendServerHeaders(HttpStatusCode status) throws IOException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT).withLocale(Locale.UK);
        headers.addHeader(HeaderName.DATE.header(), new DateTime().toDateTime(DateTimeZone.UTC).toString(formatter));
        headers.addHeader(HeaderName.SERVER.header(), APP_SERVER_DEMO);
        out.write(getBytes(DEFAULT_HTTP_VERSION + " ", String.valueOf(status.code()), " ", status.name()));
        out.write(CRLF);
        for (Header header : headers.getAllHeader()) {
            out.write(getBytes(header.getName(), ": ", header.getValue()));
            out.write(CRLF);
        }
        out.write(CRLF);
    }

    public void sendServerHeaders(HttpStatusCode status, long length, String contentType) throws IOException {
        Optional<Header> headerOptional = headers.find(HeaderName.CONTENT_TYPE.header());
        if (!headerOptional.isPresent()) {
            headers.addHeader(HeaderName.CONTENT_TYPE.header(), Optional.fromNullable(contentType).or(DEFAULT_CONTENT_TYPE));
        }
        headers.addHeader(HeaderName.CONTENT_LENGTH.header(), Long.toString(length));
        if (req!= null){
            Optional<Header> connectionHeaderOptional = req.getHeaders().find(HeaderName.CONNECTION.header());
            if (!connectionHeaderOptional.isPresent()){
                headers.addHeader(HeaderName.CONNECTION.header(), STRING_CLOSE);
            } else {
                if (req.isHttp1_1()) {
                    headers.addHeader(HeaderName.CONNECTION.header(), STRING_KEEP_ALIVE);
                } else {
                    headers.addHeader(HeaderName.CONNECTION.header(), STRING_CLOSE);
                }
            }
        }
        sendServerHeaders(status);
    }

    public void close() throws IOException {
        out.flush();
    }

    public OutputStream out() {
        return out;
    }
}
