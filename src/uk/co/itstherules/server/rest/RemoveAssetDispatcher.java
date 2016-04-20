package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;
import uk.co.itstherules.model.ObjectRepository;
import uk.co.itstherules.model.Person;
import uk.co.itstherules.model.Serializer;

public final class RemoveAssetDispatcher extends BaseHttpDispatcher {

    private final ObjectRepository repository;

    public RemoveAssetDispatcher(ObjectRepository repository) {
        this.repository = repository;
    }

    @Override protected DispatchResponse dispatch(Request request) {
        final String assetUuid = new UriParser(request.getRequestURI()).getUuid();
        repository.destroyReserveAssetByAssetId(assetUuid);
        repository.destroyAsset(assetUuid);
        return new DispatchResponse("{\"deleted\":\""+assetUuid+"\"}");
    }
}