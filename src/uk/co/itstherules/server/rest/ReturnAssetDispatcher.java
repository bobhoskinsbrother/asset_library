package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;
import uk.co.itstherules.model.*;

public final class ReturnAssetDispatcher extends BaseHttpDispatcher {

    private final ObjectRepository repository;

    public ReturnAssetDispatcher(ObjectRepository repository) {this.repository = repository;}

    @Override public DispatchResponse dispatch(Request request) {
        final String reserveAssetString = request.getParameter("return_asset");
        ReserveAsset reserveAsset = Deserializer.deserializeReserveAsset(reserveAssetString);
        try {
            repository.destroyReserveAsset(reserveAsset.getUuid());
            return new DispatchResponse (200, "application/json", "{\"status\":\"success\"}");
        } catch (NotFoundException e) {
            return new DispatchResponse (404, "application/json", "{\"status\":\"not found\"}");
        }
    }
}
