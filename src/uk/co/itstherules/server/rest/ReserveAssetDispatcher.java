package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;
import uk.co.itstherules.model.*;

public final class ReserveAssetDispatcher extends SingleObjectDispatcher {

    private final ObjectRepository repository;

    public ReserveAssetDispatcher(ObjectRepository repository) {
        this.repository = repository;
    }

    protected DispatchResponse storeAndRespond(Request request) {
        final String reserveAssetString = request.getParameter("reserveAsset");
        final ReserveAsset reserveAsset = Deserializer.deserializeReserveAsset(reserveAssetString);
        repository.add(reserveAsset);
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