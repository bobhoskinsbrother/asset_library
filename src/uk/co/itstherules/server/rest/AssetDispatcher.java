package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;
import uk.co.itstherules.model.*;

public final class AssetDispatcher extends SingleObjectDispatcher {

    private final ObjectRepository repository;

    public AssetDispatcher(ObjectRepository repository) {
        this.repository = repository;
    }

    protected DispatchResponse storeAndRespond(Request request) {
        final String assetString = request.getParameter("asset");
        final Asset asset = Deserializer.deserializeAsset(assetString);
        repository.add(asset);
        return respondWithSuccess();
    }

    protected DispatchResponse retrieveAndRespond(String uuid) {
        try {
            final Asset asset = repository.getAsset(uuid);
            String serializedAsset = Serializer.serialize(asset);
            return new DispatchResponse(serializedAsset);
        } catch (NotFoundException e) {
            return respondWithNotFound();
        }
    }

}