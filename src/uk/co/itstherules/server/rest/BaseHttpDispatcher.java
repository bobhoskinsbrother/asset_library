package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.Writer;

public abstract class BaseHttpDispatcher extends HttpHandler {

    @Override public void service(Request request, Response response) throws Exception {
        final DispatchResponse dispatchResponse = dispatch(request);
        response.setStatus(dispatchResponse.getStatus());
        response.setContentType(dispatchResponse.getContentType());
        final Writer writer = response.getWriter();
        writer.write(dispatchResponse.getBody());
        writer.close();
    }

    protected abstract DispatchResponse dispatch(Request request) throws Exception;
}
