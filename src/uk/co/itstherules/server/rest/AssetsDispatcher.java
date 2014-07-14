package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;
import uk.co.itstherules.model.Assets;
import uk.co.itstherules.model.ObjectRepository;
import uk.co.itstherules.model.Serializer;

public final class AssetsDispatcher extends BaseHttpDispatcher {

    private final ObjectRepository repository;

    public AssetsDispatcher(ObjectRepository repository) {this.repository = repository;}

    @Override public DispatchResponse dispatch(Request request) {
        final Assets assets = repository.allAssets();
        String serializedAssets = Serializer.serialize(assets);
        return new DispatchResponse(serializedAssets);
    }
}
