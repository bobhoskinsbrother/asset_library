package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.Request;

public abstract class SingleObjectDispatcher extends BaseHttpDispatcher {

    public SingleObjectDispatcher() {}

    @Override public DispatchResponse dispatch(Request request) {
        final String uuid = new UriParser(request.getRequestURI()).getUuid();
        if (isAddingOrUpdating(request)) {
            return storeAndRespond(request);
        } else if (isRetrieving(uuid, request)) {
            return retrieveAndRespond(uuid);
        }
        return respondWithNotFound();
    }

    protected abstract DispatchResponse storeAndRespond(Request request);

    protected abstract DispatchResponse retrieveAndRespond(String uuid);

    protected DispatchResponse respondWithNotFound() {return new DispatchResponse (404, "application/json", "{\"status\":\"not found\"}");}

    protected DispatchResponse respondWithSuccess() {return new DispatchResponse (200, "application/json", "{\"status\":\"success\"}");}

    private boolean isRetrieving(String uuid, Request request) { return isGetMethodUsed(request) && hasUuid(uuid); }

    private boolean hasUuid(String uuid) {return !"".equals(uuid);}

    private boolean isAddingOrUpdating(Request request) {return isPostMethodUsed(request);}

    private boolean isGetMethodUsed(Request request) {return isMethodUsed(Method.GET, request);}

    private boolean isPostMethodUsed(Request request) {return isMethodUsed(Method.POST, request);}

    private boolean isMethodUsed(Method method, Request request) {return method == request.getMethod();}
}
