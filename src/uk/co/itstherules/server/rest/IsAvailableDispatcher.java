package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;
import uk.co.itstherules.model.*;

public final class IsAvailableDispatcher extends BaseHttpDispatcher {

    private final ObjectRepository repository;

    public IsAvailableDispatcher(ObjectRepository repository) {
        this.repository = repository;
    }

    @Override protected DispatchResponse dispatch(Request request) {
        final String assetUuid = new UriParser(request.getRequestURI()).getUuid();
        boolean isAssetAvailable = repository.isAssetAvailable(assetUuid);
        StringBuilder b = new StringBuilder();
        b.append("{\"isAvailable\":").append(isAssetAvailable);
        if(!isAssetAvailable) {
            Person person = repository.personWhoReservedAsset(assetUuid);
            b.append(",\"person\":");
            b.append(Serializer.serialize(person));
        }
        b.append("}");

        return new DispatchResponse(b.toString());
    }
}