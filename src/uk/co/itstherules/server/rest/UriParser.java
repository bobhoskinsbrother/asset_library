package uk.co.itstherules.server.rest;

import uk.co.itstherules.model.Check;

import java.util.StringTokenizer;

public final class UriParser {

    private final String dispatcher;
    private final String uuid;

    public UriParser(String uri) {
        Check.that().isNotNull(uri);
        final StringTokenizer tokenizer = new StringTokenizer(uri, "/", false);
        dispatcher = (tokenizer.hasMoreTokens()) ? tokenizer.nextToken() : "";
        uuid = (tokenizer.hasMoreTokens()) ? tokenizer.nextToken() : "";
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public String getUuid() {
        return uuid;
    }
}
