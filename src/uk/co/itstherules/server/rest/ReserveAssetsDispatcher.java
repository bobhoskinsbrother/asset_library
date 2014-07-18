package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;
import uk.co.itstherules.model.ObjectRepository;
import uk.co.itstherules.model.ReserveAsset;
import uk.co.itstherules.model.Serializer;

import java.util.Collection;

public final class ReserveAssetsDispatcher extends BaseHttpDispatcher {

    private final ObjectRepository repository;

    public ReserveAssetsDispatcher(ObjectRepository repository) {this.repository = repository;}

    @Override public DispatchResponse dispatch(Request request) {
        final Collection<ReserveAsset> reserveAssets = repository.allReserveAssets();
        String serialized = Serializer.serialize(reserveAssets);
        return new DispatchResponse(serialized);
    }
}
