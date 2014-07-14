package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;

public interface Dispatcher {

    DispatchResponse dispatch(Request request);

}
