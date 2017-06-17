package io.jz.poc.appserver;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import java.util.List;

public class Headers {

    private List<Header> headers = Lists.newArrayList();

    public Header addHeader(String name, String value) {
        Header header = new Header(name, value);
        headers.add(header);
        return header;
    }

    public List<Header> getAllHeader() {
        return headers;
    }

    public Optional<Header> find(final String headerName) {
        return FluentIterable.from(headers).filter(new Predicate<Header>() {
            public boolean apply(Header header) {
                return headerName.equals(header.getName());
            }
        }).first();
    }

    public boolean validate(String header, String value) {
        Optional<Header> headerOptional = find(header);
        return headerOptional.isPresent() && value.equalsIgnoreCase(headerOptional.get().getValue());
    }
}
